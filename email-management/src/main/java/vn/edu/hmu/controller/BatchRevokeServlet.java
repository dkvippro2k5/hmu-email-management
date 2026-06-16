package vn.edu.hmu.controller;

import org.apache.poi.ss.usermodel.*;
import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.ActionLog;
import vn.edu.hmu.util.DBConnection;

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        Part filePart = request.getPart("excelFile");
        if (filePart == null || filePart.getSize() == 0) {
            request.getSession().setAttribute("errorMsg", "Vui lòng chọn file Excel để thu hồi!");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        int successCount = 0;
        int errorCount = 0;
        List<String> errorDetails = new ArrayList<>();

        try (InputStream fileContent = filePart.getInputStream();
             Workbook workbook = WorkbookFactory.create(fileContent);
             Connection conn = DBConnection.getConnection()) {

            Sheet sheet = workbook.getSheetAt(0);
            String updateSql = "UPDATE email_accounts SET status = 3, scheduled_delete_date = DATE_ADD(NOW(), INTERVAL 30 DAY) WHERE email_address = ? OR student_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                // Bỏ qua dòng tiêu đề (i = 1)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    String fullName = getSafeString(row.getCell(1));
                    String col2 = getSafeString(row.getCell(2));
                    String col3 = getSafeString(row.getCell(3));
                    String col4 = getSafeString(row.getCell(4));

                    if (fullName.isEmpty() && col2.isEmpty() && col3.isEmpty() && col4.isEmpty()) {
                        continue;
                    }

                    String email = "";
                    String studentId = "";

                    String[] cols = {col2, col3, col4};
                    for (String c : cols) {
                        if (c.contains("@")) {
                            email = c;
                        } else if (c.matches("\\d{8}") || c.matches("HV\\d+")) {
                            studentId = c;
                        }
                    }

                    if (email.isEmpty() && studentId.isEmpty()) {
                        errorCount++;
                        errorDetails.add("Dòng " + (i + 1) + ": Không tìm thấy Email hoặc Mã SV hợp lệ.");
                        continue;
                    }

                    ps.setString(1, email);
                    ps.setString(2, studentId);
                    int affectedRows = ps.executeUpdate();

                    if (affectedRows > 0) {
                        successCount++;
                        ActionLog log = new ActionLog();
                        log.setActionType("REVOKE_BATCH");
                        log.setTargetEmail(email.isEmpty() ? studentId : email);
                        log.setReason("Thu hồi hàng loạt từ Excel. Hẹn xóa sau 30 ngày.");
                        adminDAO.insertActionLog(log);
                    } else {
                        errorCount++;
                        errorDetails.add("Dòng " + (i + 1) + ": Không tìm thấy tài khoản " + (email.isEmpty() ? studentId : email) + " trên hệ thống.");
                    }
                }
            }
            
            // Ghi nhận tổng quan
            ActionLog summaryLog = new ActionLog();
            summaryLog.setActionType("BATCH_RESULT");
            summaryLog.setTargetEmail("SYSTEM");
            summaryLog.setReason("Kết quả thu hồi Excel: " + successCount + " thành công, " + errorCount + " lỗi. Email thông báo đã được gửi giả lập.");
            adminDAO.insertActionLog(summaryLog);

            StringBuilder finalMsg = new StringBuilder();
            if (successCount > 0) {
                finalMsg.append("Đã đưa ").append(successCount).append(" tài khoản vào danh sách chờ xóa sau 30 ngày! Hệ thống đã mô phỏng việc gửi email thông báo tới sinh viên.");
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

        response.sendRedirect(request.getContextPath() + "/dashboard");
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
