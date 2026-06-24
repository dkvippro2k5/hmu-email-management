package vn.edu.hmu.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.ActionLog;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.ITAdmin;
import vn.edu.hmu.util.EmailService;
import vn.edu.hmu.util.FileStorageUtil;
import vn.edu.hmu.dao.ArchiveDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/batch-suspend")
@MultipartConfig
public class BatchSuspendServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, java.io.IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // Kiểm tra quyền Admin
        ITAdmin admin = (ITAdmin) request.getSession().getAttribute("currentAdmin");
        if (admin == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String decisionNumber = request.getParameter("decisionNumber");
        if (decisionNumber == null || !decisionNumber.matches(".*\\/QĐ-ĐHYHN.*")) {
            request.getSession().setAttribute("errorMsg", "Số quyết định không hợp lệ. Phải chứa chuỗi '/QĐ-ĐHYHN'.");
            response.sendRedirect("dashboard#page-archive");
            return;
        }

        Part filePart = request.getPart("excelFile"); 
        if (filePart == null || filePart.getSize() == 0) {
            request.getSession().setAttribute("errorMsg", "Vui lòng chọn file Excel.");
            response.sendRedirect("dashboard#page-archive");
            return;
        }

        String savedPath = FileStorageUtil.saveUploadedFile(filePart, "M02_BAOLUU", admin.getAdminID());
        if (savedPath == null) {
            request.getSession().setAttribute("errorMsg", "Lỗi lưu file hệ thống.");
            response.sendRedirect("dashboard#page-archive");
            return;
        }
        
        int adminIdInt = 0;
        try {
            adminIdInt = Integer.parseInt(admin.getAdminID());
        } catch (Exception e) {}

        // Lưu vào kho archive_m02
        ArchiveDAO archiveDAO = new ArchiveDAO();
        archiveDAO.insertArchiveM02("BAO_LUU", decisionNumber, filePart.getSubmittedFileName(), savedPath, adminIdInt);

        InputStream fileContent = filePart.getInputStream();
        StudentDAO studentDAO = new StudentDAO();
        AdminDAO adminDAO = new AdminDAO();
        
        int successCount = 0;
        List<String> errorMessages = new ArrayList<>();
        List<String> suspendedEmails = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(fileContent)) {
            Sheet sheet = workbook.getSheetAt(0); 

            // Bỏ qua dòng tiêu đề
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // Cột M.02: 1(Họ tên), 2(Email), 3(Mã SV), 4(Niên khóa).
                    String email = getSafeString(row.getCell(2));
                    String studentId = getSafeString(row.getCell(3));

                    if (email.isEmpty() && studentId.isEmpty()) {
                        continue; 
                    }

                    EmailAccount acc = null;
                    if (!email.isEmpty()) {
                        acc = studentDAO.getAccountByEmail(email);
                    }
                    if (acc == null && !studentId.isEmpty()) {
                        // Nếu cần có thể tìm theo mã sinh viên, tạm thời thông qua email
                    }

                    if (acc == null) {
                        errorMessages.add("Dòng " + (i+1) + ": Email " + email + " không tồn tại trong hệ thống.");
                        continue;
                    }
                    
                    try {
                        EmailService.syncSuspendWithCloud(email);
                        
                        boolean suspended = studentDAO.suspendAccount(acc.getStudentId(), decisionNumber);
                        if (suspended) {
                            successCount++;
                            suspendedEmails.add(email);
                            
                            ActionLog detailLog = new ActionLog();
                            detailLog.setActionType("SUSPEND_ACCOUNT");
                            detailLog.setTargetEmail(email);
                            detailLog.setReason("Bảo lưu tài khoản qua Excel M.02. QĐ: " + decisionNumber);
                            detailLog.setDetails("Bảo lưu thành công");
                            detailLog.setAdminId(adminIdInt);
                            adminDAO.insertActionLog(detailLog);
                        } else {
                            errorMessages.add("Dòng " + (i+1) + ": Lỗi cập nhật Database cho email " + email);
                        }
                        
                    } catch (Exception ex) {
                        errorMessages.add("Dòng " + (i+1) + ": Đồng bộ Cloud thất bại cho " + email + " - Lỗi: " + ex.getMessage());
                        ActionLog errorLog = new ActionLog(adminIdInt, email, "SUSPEND_ERROR", "Lỗi đồng bộ Cloud: " + ex.getMessage(), null);
                        adminDAO.insertActionLog(errorLog);
                    }

                } catch (Exception ex) {
                    errorMessages.add("Dòng " + (i+1) + ": Lỗi không xác định - " + ex.getMessage());
                }
            }

            if (successCount > 0) {
                ActionLog summaryLog = new ActionLog();
                summaryLog.setActionType("BATCH_SUSPEND");
                summaryLog.setTargetEmail(null);
                summaryLog.setReason("Bảo lưu hàng loạt. QĐ: " + decisionNumber + " - Thành công: " + successCount);
                summaryLog.setAdminId(adminIdInt);

                StringBuilder detailsBuilder = new StringBuilder("[");
                for (int i = 0; i < suspendedEmails.size(); i++) {
                    detailsBuilder.append("\"").append(suspendedEmails.get(i)).append("\"");
                    if (i < suspendedEmails.size() - 1) detailsBuilder.append(",");
                }
                detailsBuilder.append("]");
                summaryLog.setDetails(detailsBuilder.toString());
                adminDAO.insertActionLog(summaryLog);
            }

            StringBuilder finalMsg = new StringBuilder();
            if (successCount > 0) {
                finalMsg.append("✅ Đã bảo lưu thành công ").append(successCount).append(" tài khoản.<br>");
            }
            if (!errorMessages.isEmpty()) {
                finalMsg.append("❌ Có ").append(errorMessages.size()).append(" lỗi xảy ra:<br>");
                int count = 0;
                for (String err : errorMessages) {
                    if (count >= 5) {
                        finalMsg.append("... và ").append(errorMessages.size() - 5).append(" lỗi khác.");
                        break;
                    }
                    finalMsg.append("- ").append(err).append("<br>");
                    count++;
                }
                
                if (successCount > 0) {
                    request.getSession().setAttribute("successMsg", finalMsg.toString());
                } else {
                    request.getSession().setAttribute("errorMsg", finalMsg.toString());
                }
            } else if (successCount > 0) {
                request.getSession().setAttribute("successMsg", finalMsg.toString());
            }

            response.sendRedirect("dashboard#page-archive");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Lỗi đọc file Excel: " + e.getMessage());
            response.sendRedirect("dashboard#page-archive");
        }
    }

    private String getSafeString(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
