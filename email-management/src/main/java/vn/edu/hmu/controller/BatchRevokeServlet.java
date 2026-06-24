package vn.edu.hmu.controller;

import org.apache.poi.ss.usermodel.*;
import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.ActionLog;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.ITAdmin;
import vn.edu.hmu.util.DBConnection;
import vn.edu.hmu.util.EmailService;
import vn.edu.hmu.util.FileStorageUtil;
import vn.edu.hmu.dao.ArchiveDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/batch-revoke")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class BatchRevokeServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();
    private AdminDAO adminDAO = new AdminDAO();
    private ArchiveDAO archiveDAO = new ArchiveDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        ITAdmin admin = (ITAdmin) request.getSession().getAttribute("currentAdmin");
        if (admin == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String customNotification = request.getParameter("customNotification");
        String decisionNumber = request.getParameter("decisionNumber");
        if (decisionNumber == null || !decisionNumber.matches(".*\\/QĐ-ĐHYHN.*")) {
            request.getSession().setAttribute("errorMsg", "Số quyết định không hợp lệ. Phải chứa chuỗi '/QĐ-ĐHYHN'.");
            response.sendRedirect("dashboard#page-archive");
            return;
        }

        Part filePart = request.getPart("excelFile");
        if (filePart == null || filePart.getSize() == 0) {
            request.getSession().setAttribute("errorMsg", "Vui lòng chọn file Excel để thu hồi!");
            response.sendRedirect(request.getContextPath() + "/dashboard#page-archive");
            return;
        }

        String appPath = request.getServletContext().getRealPath("");
        String savedPath = FileStorageUtil.saveUploadedFile(filePart, "M02_THUHOI", admin.getAdminID());
        if (savedPath == null) {
            request.getSession().setAttribute("errorMsg", "Lỗi lưu file hệ thống.");
            response.sendRedirect("dashboard#page-archive");
            return;
        }

        int adminIdInt = 0;
        try {
            adminIdInt = Integer.parseInt(admin.getAdminID());
        } catch(Exception ignored) {}

        // archiveDAO.insertArchiveM02("THU_HOI", decisionNumber, filePart.getSubmittedFileName(), savedPath, adminIdInt);

        int successCount = 0;
        int errorCount = 0;
        List<String> errorDetails = new ArrayList<>();
        List<String> revokedEmails = new ArrayList<>();

        try (InputStream fileContent = filePart.getInputStream();
             Workbook workbook = WorkbookFactory.create(fileContent);
             Connection conn = DBConnection.getConnection()) {

            Sheet sheet = workbook.getSheetAt(0);
            String updateSql = "UPDATE email_accounts SET status = 3, scheduled_delete_date = DATE_ADD(NOW(), INTERVAL 30 DAY), decision_number = ? WHERE email_address = ? OR student_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                // Tìm dòng tiêu đề
                int dataStartRow = 1;
                for (int i = 0; i <= Math.min(10, sheet.getLastRowNum()); i++) {
                    Row r = sheet.getRow(i);
                    if (r == null) continue;
                    boolean hasEmail = false;
                    for (Cell c : r) {
                        String val = getSafeString(c).toLowerCase().trim();
                        if (val.contains("email")) hasEmail = true;
                    }
                    if (hasEmail) {
                        dataStartRow = i + 1;
                        break;
                    }
                }

                for (int i = dataStartRow; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    String fullName = getSafeString(row.getCell(1));
                    String col2 = getSafeString(row.getCell(2)); // Email
                    String col3 = getSafeString(row.getCell(3)); // Mã SV

                    String email = "";
                    String studentId = "";

                    if (col2.contains("@")) {
                        email = col2;
                    } else if (col3.contains("@")) {
                        email = col3;
                    }

                    if (col3.matches("\\d{8}") || col3.matches("HV\\d+")) {
                        studentId = col3;
                    } else if (col2.matches("\\d{8}") || col2.matches("HV\\d+")) {
                        studentId = col2;
                    }

                    if (email.isEmpty() && studentId.isEmpty()) {
                        continue;
                    }

                    EmailAccount acc = null;
                    if (!email.isEmpty()) {
                        acc = studentDAO.getAccountByEmail(email);
                    }

                    ps.setString(1, decisionNumber);
                    ps.setString(2, email);
                    ps.setString(3, studentId);
                    int affectedRows = ps.executeUpdate();
                    
                    if (affectedRows > 0 && acc != null) {
                        archiveDAO.insertArchiveM02("THU_HOI", decisionNumber, (i + 1),
                                fullName.isEmpty() ? acc.getStudentName() : fullName,
                                acc.getEmailAddress(), acc.getStudentId(), "N/A", adminIdInt);
                    }

                    if (affectedRows > 0) {
                        successCount++;
                        
                        if (acc != null && customNotification != null && !customNotification.trim().isEmpty()) {
                            vn.edu.hmu.model.Notification notif = new vn.edu.hmu.model.Notification();
                            notif.setStudentId(acc.getStudentId());
                            notif.setTitle("THÔNG BÁO TỪ PHÒNG IT");
                            notif.setMessage(customNotification);
                            notif.setCreatedAt(new java.util.Date());
                            new vn.edu.hmu.dao.NotificationDAO().insertNotification(notif);
                        }
                        
                        // Gửi email thu hồi ngầm (Background Thread)
                        if (acc != null) {
                            final String targetEmail = acc.getEmailAddress();
                            final String studentName = acc.getStudentName();
                            final int threadDelay = successCount * 500; // Delay tăng dần
                            
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(threadDelay);
                                        String mailContent = EmailService.sendRevokeWarningEmail(targetEmail, studentName);
                                        if (mailContent != null) {
                                            vn.edu.hmu.dao.ArchiveDAO aDao = new vn.edu.hmu.dao.ArchiveDAO();
                                            aDao.insertArchivePL01(targetEmail, studentName, decisionNumber, mailContent);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        
                        ActionLog detailLog = new ActionLog();
                        detailLog.setActionType("DELETE");
                        detailLog.setTargetEmail(acc != null ? acc.getEmailAddress() : email);
                        detailLog.setReason("Thu hồi tài khoản qua Excel. QĐ: " + decisionNumber);
                        detailLog.setDetails("Chờ xóa sau 30 ngày");
                        try {
                            detailLog.setAdminId(Integer.parseInt(admin.getAdminID()));
                        } catch(Exception ignored) {}
                        adminDAO.insertActionLog(detailLog);

                        revokedEmails.add(email.isEmpty() ? studentId : email);
                    } else {
                        errorCount++;
                        errorDetails.add("Dòng " + (i + 1) + ": Không tìm thấy tài khoản " + (email.isEmpty() ? studentId : email) + " trên hệ thống.");
                    }
                }
            }
            
            ActionLog summaryLog = new ActionLog();
            summaryLog.setActionType("BATCH_REVOKE");
            summaryLog.setTargetEmail(null);
            summaryLog.setReason("Kết quả thu hồi Excel. QĐ: " + decisionNumber + " - Thành công: " + successCount + ", Lỗi: " + errorCount);
            
            StringBuilder detailsBuilder = new StringBuilder("[");
            for (int i = 0; i < revokedEmails.size(); i++) {
                detailsBuilder.append("\"").append(revokedEmails.get(i)).append("\"");
                if (i < revokedEmails.size() - 1) detailsBuilder.append(",");
            }
            detailsBuilder.append("]");
            summaryLog.setDetails(detailsBuilder.toString());
            
            try {
                summaryLog.setAdminId(Integer.parseInt(admin.getAdminID()));
            } catch(Exception ignored) {}
            adminDAO.insertActionLog(summaryLog);

            StringBuilder finalMsg = new StringBuilder();
            if (successCount > 0) {
                finalMsg.append("Đã đưa ").append(successCount).append(" tài khoản vào danh sách chờ xóa sau 30 ngày! Hệ thống đã gửi email cảnh báo tự động.");
            }
            if (errorCount > 0) {
                if (successCount > 0) finalMsg.append("<br>");
                finalMsg.append("Có ").append(errorCount).append(" dòng lỗi:<br>");
                for (String err : errorDetails) {
                    finalMsg.append("- ").append(err).append("<br>");
                }
            }

            if (successCount == 0 && errorCount > 0) {
                request.getSession().setAttribute("errorMsg", finalMsg.toString());
            } else if (successCount > 0) {
                request.getSession().setAttribute("successMsg", finalMsg.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Lỗi xử lý file Excel: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/dashboard#page-archive");
    }

    private String getSafeString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                long val = (long) cell.getNumericCellValue();
                return String.valueOf(val);
            default:
                return "";
        }
    }
}
