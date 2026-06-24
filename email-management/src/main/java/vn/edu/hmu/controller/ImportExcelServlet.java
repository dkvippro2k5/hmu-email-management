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
import vn.edu.hmu.util.AESUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/import-excel")
@MultipartConfig
public class ImportExcelServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();
    private AdminDAO adminDAO = new AdminDAO();
    private ArchiveDAO archiveDAO = new ArchiveDAO();

    private static class ImportRow {
        int rowNum;
        String studentId, fullName, cccd, firstName, lastName, cohort, phone, importedEmail, importedPassword;
        Student studentObj;
        EmailAccount emailAccObj;
        ActionLog detailLogObj;
        String errorMessage;
    }

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
            // Bỏ dòng lưu archive_m01 nguyên file
        } catch (Exception ignored) {}

        int successCount = 0;
        int errorCount = 0;
        List<String> errorDetails = new ArrayList<>();
        List<String> createdStudents = new ArrayList<>();

        try (InputStream fileContent = filePart.getInputStream();
             Workbook workbook = WorkbookFactory.create(fileContent)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Tìm dòng tiêu đề
            Row headerRow = null;
            int dataStartRow = 0;
            for (int i = 0; i <= Math.min(10, sheet.getLastRowNum()); i++) {
                Row r = sheet.getRow(i);
                if (r == null) continue;
                boolean hasSTT = false;
                boolean hasEmail = false;
                for (Cell c : r) {
                    String val = getSafeString(c).toLowerCase().trim();
                    if (val.contains("stt")) hasSTT = true;
                    if (val.contains("email")) hasEmail = true;
                }
                if (hasSTT && hasEmail) {
                    headerRow = r;
                    dataStartRow = i + 1;
                    break;
                }
            }

            if (headerRow == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy dòng tiêu đề hợp lệ trong file Excel!");
                response.sendRedirect("dashboard");
                return;
            }

            // Map các cột
            Map<String, Integer> colMap = new HashMap<>();
            for (Cell c : headerRow) {
                String val = getSafeString(c).toLowerCase().trim();
                if (val.contains("họ tên") || val.contains("ho ten")) colMap.put("fullName", c.getColumnIndex());
                else if (val.contains("cccd")) colMap.put("cccd", c.getColumnIndex());
                else if (val.contains("email") && !val.contains("cá nhân") && !val.contains("ca nhan")) colMap.put("email", c.getColumnIndex());
                else if (val.contains("password") || val.contains("mật khẩu")) colMap.put("password", c.getColumnIndex());
                else if (val.equals("tên") || val.equals("ten")) colMap.put("firstName", c.getColumnIndex());
                else if (val.contains("họ đệm") || val.contains("ho dem")) colMap.put("lastName", c.getColumnIndex());
                else if (val.contains("mã sinh viên") || val.contains("ma sinh vien") || val.contains("mssv")) colMap.put("studentId", c.getColumnIndex());
                else if (val.contains("niên khóa") || val.contains("nien khoa")) colMap.put("cohort", c.getColumnIndex());
                else if (val.contains("điện thoại") || val.contains("dien thoai") || val.contains("sđt")) colMap.put("phone", c.getColumnIndex());
            }

            List<ImportRow> rowsData = new ArrayList<>();
            for (int i = dataStartRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                ImportRow ir = new ImportRow();
                ir.rowNum = i + 1;
                ir.fullName = colMap.containsKey("fullName") ? getSafeString(row.getCell(colMap.get("fullName"))) : "";
                ir.cccd = colMap.containsKey("cccd") ? getSafeString(row.getCell(colMap.get("cccd"))) : "";
                ir.firstName = colMap.containsKey("firstName") ? getSafeString(row.getCell(colMap.get("firstName"))) : "";
                ir.lastName = colMap.containsKey("lastName") ? getSafeString(row.getCell(colMap.get("lastName"))) : "";
                ir.studentId = colMap.containsKey("studentId") ? getSafeString(row.getCell(colMap.get("studentId"))) : "";
                ir.cohort = colMap.containsKey("cohort") ? getSafeString(row.getCell(colMap.get("cohort"))) : "";
                ir.phone = colMap.containsKey("phone") ? getSafeString(row.getCell(colMap.get("phone"))) : "";
                ir.importedEmail = colMap.containsKey("email") ? getSafeString(row.getCell(colMap.get("email"))) : "";
                ir.importedPassword = colMap.containsKey("password") ? getSafeString(row.getCell(colMap.get("password"))) : "";
                
                if (ir.fullName.isEmpty() || ir.studentId.isEmpty() || ir.importedEmail.isEmpty() || ir.importedPassword.isEmpty()) {
                    continue;
                }
                rowsData.add(ir);
                archiveDAO.insertArchiveM01(ir.rowNum, ir.fullName, ir.importedEmail, ir.studentId, ir.cohort, Integer.parseInt(admin.getAdminID()));
            }

            int finalAdminId = 1;
            try { finalAdminId = Integer.parseInt(admin.getAdminID()); } catch(Exception ignored) {}
            final int fAdminId = finalAdminId;

            rowsData.parallelStream().forEach(ir -> {
                try {
                    String portalPasswordHash = "";
                    if (!ir.cccd.isEmpty()) {
                        portalPasswordHash = org.mindrot.jbcrypt.BCrypt.hashpw(ir.cccd, org.mindrot.jbcrypt.BCrypt.gensalt());
                    }
                    String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(ir.importedPassword, org.mindrot.jbcrypt.BCrypt.gensalt());
                    String encryptedInitialPassword = AESUtil.encrypt(ir.importedPassword);

                    ir.studentObj = new Student(ir.studentId, ir.fullName, ir.cccd, ir.firstName, ir.lastName, ir.cohort, ir.phone, portalPasswordHash);
                    
                    ir.emailAccObj = new EmailAccount();
                    ir.emailAccObj.setEmailAddress(ir.importedEmail);
                    ir.emailAccObj.setStudentId(ir.studentId);
                    ir.emailAccObj.setPasswordHash(passwordHash);
                    ir.emailAccObj.setInitialPasswordEncrypted(encryptedInitialPassword);
                    ir.emailAccObj.setStatus(0);

                    ir.detailLogObj = new ActionLog();
                    ir.detailLogObj.setActionType("CREATE_ACCOUNT");
                    ir.detailLogObj.setTargetEmail(ir.importedEmail);
                    ir.detailLogObj.setReason("Import tài khoản từ Excel");
                    ir.detailLogObj.setDetails("MSSV: " + ir.studentId);
                    ir.detailLogObj.setAdminId(fAdminId);

                } catch (Exception e) {
                    ir.errorMessage = "Dòng " + ir.rowNum + ": Lỗi xử lý mã hóa - " + e.getMessage();
                }
            });

            List<Student> batchStudents = new ArrayList<>();
            List<EmailAccount> batchEmails = new ArrayList<>();
            List<ActionLog> batchLogs = new ArrayList<>();

            for (ImportRow ir : rowsData) {
                if (ir.errorMessage != null) {
                    errorCount++;
                    errorDetails.add(ir.errorMessage);
                } else if (ir.studentObj != null) {
                    batchStudents.add(ir.studentObj);
                    batchEmails.add(ir.emailAccObj);
                    batchLogs.add(ir.detailLogObj);
                }
            }

            if (!batchStudents.isEmpty()) {
                boolean batchSuccess = studentDAO.createStudentsWithEmailsBatch(batchStudents, batchEmails);
                if (batchSuccess) {
                    adminDAO.insertActionLogsBatch(batchLogs);
                    successCount += batchStudents.size();
                    for(Student s : batchStudents) createdStudents.add(s.getStudentId());
                } else {
                    errorCount += batchStudents.size();
                    errorDetails.add("Lỗi chèn Database hàng loạt! Có thể có sinh viên đã tồn tại hoặc trùng lặp ID.");
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