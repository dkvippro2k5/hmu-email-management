package vn.edu.hmu.controller;

import org.apache.poi.ss.usermodel.*;
import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.ActionLog;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.ITAdmin;
import vn.edu.hmu.model.Student;
import vn.edu.hmu.dao.ArchiveDAO;
import vn.edu.hmu.util.AccountGenerator;
import vn.edu.hmu.util.FileStorageUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/import-excel")
@MultipartConfig
public class ImportExcelServlet extends HttpServlet {

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

        Part filePart = request.getPart("excelFile");
        if (filePart == null || filePart.getSize() == 0) {
            request.getSession().setAttribute("errorMsg", "Vui lòng chọn file Excel M.01!");
            response.sendRedirect("dashboard");
            return;
        }

        String appPath = request.getServletContext().getRealPath("");
        String savedPath = FileStorageUtil.saveUploadedFile(filePart, "M01_KHOITAO", admin.getAdminID());
        if (savedPath == null) {
            request.getSession().setAttribute("errorMsg", "Lỗi lưu file hệ thống.");
            response.sendRedirect("dashboard");
            return;
        }

        // Lưu vào kho archive_m01
        try {
            archiveDAO.insertArchiveM01(filePart.getSubmittedFileName(), savedPath, Integer.parseInt(admin.getAdminID()));
        } catch (Exception ignored) {}

        int successCount = 0;
        int errorCount = 0;
        List<String> errorDetails = new ArrayList<>();
        List<String> createdStudents = new ArrayList<>();

        try (InputStream fileContent = filePart.getInputStream();
             Workbook workbook = WorkbookFactory.create(fileContent)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Bỏ qua dòng tiêu đề (thường dòng 1 và 2 là tiêu đề)
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // Đọc đúng vị trí cột M.01: 
                    // 1: Họ tên đầy đủ, 2: CCCD, 5: Tên, 6: Họ đệm, 7: Mã sinh viên, 8: Niên khóa, 9: SĐT
                    String fullName = getSafeString(row.getCell(1));
                    String cccd = getSafeString(row.getCell(2));
                    String firstName = getSafeString(row.getCell(5));
                    String lastName = getSafeString(row.getCell(6));
                    String studentId = getSafeString(row.getCell(7));
                    String cohort = getSafeString(row.getCell(8));
                    String phone = getSafeString(row.getCell(9));

                    if (fullName.isEmpty() || studentId.isEmpty() || firstName.isEmpty()) {
                        continue;
                    }

                    // Tự động sinh Email và Mật khẩu
                    String emailAddress = AccountGenerator.generateEmail(firstName, lastName, studentId);
                    String rawPassword = AccountGenerator.generateDefaultPassword();
                    String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt());

                    Student student = new Student(studentId, fullName, cccd, firstName, lastName, cohort, phone);
                    EmailAccount emailAcc = new EmailAccount();
                    emailAcc.setEmailAddress(emailAddress);
                    emailAcc.setStudentId(studentId);
                    emailAcc.setPasswordHash(passwordHash);
                    emailAcc.setStatus(0); // 0: Chờ kích hoạt

                    boolean success = studentDAO.createStudentWithEmail(student, emailAcc);
                    if (success) {
                        successCount++;
                        createdStudents.add(studentId);
                        
                        ActionLog detailLog = new ActionLog();
                        detailLog.setActionType("CREATE_ACCOUNT");
                        detailLog.setTargetEmail(emailAddress);
                        detailLog.setReason("Tạo tài khoản qua Import Excel M.01");
                        detailLog.setDetails("Tạo mới tài khoản cho sinh viên: " + fullName + " - MSSV: " + studentId);
                        try {
                            detailLog.setAdminId(Integer.parseInt(admin.getAdminID()));
                        } catch(Exception ignored) {}
                        adminDAO.insertActionLog(detailLog);
                    } else {
                        errorCount++;
                        errorDetails.add("Dòng " + (i + 1) + ": Không thể lưu vào DB (Mã SV " + studentId + " có thể đã tồn tại).");
                    }
                } catch (Exception e) {
                    errorCount++;
                    errorDetails.add("Dòng " + (i + 1) + ": Lỗi định dạng - " + e.getMessage());
                }
            }

            ActionLog summaryLog = new ActionLog();
            summaryLog.setActionType("BATCH_IMPORT");
            summaryLog.setTargetEmail(null);
            summaryLog.setReason("Khởi tạo hàng loạt Excel M.01: " + successCount + " thành công, " + errorCount + " lỗi.");
            
            StringBuilder detailsBuilder = new StringBuilder("[");
            for (int i = 0; i < createdStudents.size(); i++) {
                detailsBuilder.append("\"").append(createdStudents.get(i)).append("\"");
                if (i < createdStudents.size() - 1) detailsBuilder.append(",");
            }
            detailsBuilder.append("]");
            summaryLog.setDetails(detailsBuilder.toString());
            
            try {
                summaryLog.setAdminId(Integer.parseInt(admin.getAdminID()));
            } catch(Exception ignored) {}
            adminDAO.insertActionLog(summaryLog);

            StringBuilder finalMsg = new StringBuilder();
            if (successCount > 0) {
                finalMsg.append("Đã khởi tạo thành công ").append(successCount).append(" tài khoản từ file M.01!");
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

        response.sendRedirect("dashboard");
    }

    private String getSafeString(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}