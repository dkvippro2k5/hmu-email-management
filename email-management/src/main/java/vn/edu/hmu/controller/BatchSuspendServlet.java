package vn.edu.hmu.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.ActionLog;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.ITAdmin;
import vn.edu.hmu.util.EmailService;

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

        Part filePart = request.getPart("excelFile"); 
        if (filePart == null || filePart.getSize() == 0) {
            request.getSession().setAttribute("errorMsg", "Vui lòng chọn file Excel.");
            response.sendRedirect("dashboard");
            return;
        }

        InputStream fileContent = filePart.getInputStream();
        StudentDAO studentDAO = new StudentDAO();
        AdminDAO adminDAO = new AdminDAO();
        
        int adminIdInt = 0;
        try {
            adminIdInt = Integer.parseInt(admin.getAdminID());
        } catch (Exception e) {
            System.out.println("Không thể chuyển đổi adminID sang int: " + admin.getAdminID());
        }
        
        int successCount = 0;
        List<String> errorMessages = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(fileContent)) {
            Sheet sheet = workbook.getSheetAt(0); 

            // Bỏ qua dòng tiêu đề (index 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // Cấu trúc file: 0: Họ tên, 1: Email, 2: Mã SV, 3: Niên khóa
                    String fullName = getSafeString(row.getCell(0));
                    String email = getSafeString(row.getCell(1));
                    String studentId = getSafeString(row.getCell(2));

                    if (email.isEmpty() || studentId.isEmpty()) {
                        errorMessages.add("Dòng " + (i+1) + ": Bị bỏ qua do thiếu Email hoặc Mã SV. (Email: '" + email + "', Mã SV: '" + studentId + "')");
                        continue; 
                    }

                    // 1. Kiểm tra tài khoản có tồn tại không
                    EmailAccount acc = studentDAO.getAccountByEmail(email);
                    if (acc == null) {
                        errorMessages.add("Dòng " + (i+1) + ": Email " + email + " không tồn tại trong hệ thống.");
                        continue;
                    }
                    
                    // 2. Đồng bộ với Cloud (Mô phỏng)
                    try {
                        EmailService.syncSuspendWithCloud(email);
                        
                        // 3. Nếu Cloud thành công, cập nhật Database bằng mã SV chuẩn từ hệ thống
                        boolean suspended = studentDAO.suspendAccount(acc.getStudentId(), "Quy chế bảo lưu (Từ file Excel)");
                        if (suspended) {
                            successCount++;
                            // 4. Ghi Log
                            ActionLog log = new ActionLog(adminIdInt, email, "SUSPEND_BATCH", "Bảo lưu hàng loạt từ Excel");
                            adminDAO.insertActionLog(log);
                        } else {
                            errorMessages.add("Dòng " + (i+1) + ": Lỗi cập nhật Database cho email " + email);
                        }
                        
                    } catch (Exception ex) {
                        // Nếu Cloud lỗi (vd API Timeout), bắt ngoại lệ và không cập nhật DB nội bộ
                        errorMessages.add("Dòng " + (i+1) + ": Đồng bộ Cloud thất bại cho " + email + " - Lỗi: " + ex.getMessage());
                        // Ghi log lỗi để admin dễ theo dõi
                        ActionLog errorLog = new ActionLog(adminIdInt, email, "SUSPEND_ERROR", "Lỗi đồng bộ Cloud: " + ex.getMessage());
                        adminDAO.insertActionLog(errorLog);
                    }

                } catch (Exception ex) {
                    errorMessages.add("Dòng " + (i+1) + ": Lỗi không xác định - " + ex.getMessage());
                }
            }

            // Lưu kết quả vào Session và Ghi log tổng hợp
            StringBuilder finalMsg = new StringBuilder();
            if (successCount > 0) {
                finalMsg.append("✅ Đã bảo lưu thành công ").append(successCount).append(" tài khoản.<br>");
            }
            if (!errorMessages.isEmpty()) {
                finalMsg.append("❌ Có ").append(errorMessages.size()).append(" lỗi xảy ra (Vui lòng tự khóa thủ công):<br>");
                int count = 0;
                for (String err : errorMessages) {
                    if (count >= 5) {
                        finalMsg.append("... và ").append(errorMessages.size() - 5).append(" lỗi khác.");
                        break;
                    }
                    finalMsg.append("- ").append(err).append("<br>");
                    count++;
                }
                request.getSession().setAttribute("errorMsg", finalMsg.toString());
            } else if (successCount > 0) {
                request.getSession().setAttribute("successMsg", finalMsg.toString());
            }

            // Ghi 1 dòng log tổng quát về quá trình này
            if (successCount > 0 || !errorMessages.isEmpty()) {
                String summaryStr = "Xử lý hàng loạt: " + successCount + " thành công, " + errorMessages.size() + " lỗi.";
                ActionLog summaryLog = new ActionLog(adminIdInt, "Hệ thống (Batch)", "BATCH_RESULT", summaryStr);
                adminDAO.insertActionLog(summaryLog);
            }

            response.sendRedirect("dashboard#page-archive");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Lỗi đọc file Excel: " + e.getMessage());
            response.sendRedirect("dashboard");
        }
    }

    private String getSafeString(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
