package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/suspend-account")
public class SuspendAccountServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Cấu hình định dạng trả về là JSON và hỗ trợ tiếng Việt có dấu
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8"); // Đảm bảo đọc đúng Số quyết định bằng tiếng Việt
        
        PrintWriter out = response.getWriter();
        
        // 2. Lấy dữ liệu từ Frontend gửi lên (Method POST)
        String studentId = request.getParameter("studentId");
        String decisionNumber = request.getParameter("decisionNumber");
        
        // 3. Kiểm tra dữ liệu an toàn
        if (studentId == null || studentId.trim().isEmpty() || 
            decisionNumber == null || decisionNumber.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Thiếu dữ liệu: Mã sinh viên hoặc Số quyết định!\"}");
            out.flush();
            return;
        }
        
        // Bỏ regex validation khắt khe để admin có thể nhập các định dạng khác
        // if (!decisionNumber.matches(".*\\/QĐ-ĐHYHN.*")) { ... }

        // 4. Gọi DAO cập nhật xuống Database
        StudentDAO dao = new StudentDAO();
        boolean isSuccess = dao.suspendAccount(studentId, decisionNumber);
        
        // 5. Trả kết quả về cho Frontend
        if (isSuccess) {
            try {
                vn.edu.hmu.model.ITAdmin admin = (vn.edu.hmu.model.ITAdmin) request.getSession().getAttribute("currentAdmin");
                if (admin != null) {
                    vn.edu.hmu.dao.AdminDAO adminDAO = new vn.edu.hmu.dao.AdminDAO();
                    vn.edu.hmu.model.ActionLog log = new vn.edu.hmu.model.ActionLog();
                    int safeAdminId = 1;
                    try { if (admin.getAdminID() != null) safeAdminId = Integer.parseInt(admin.getAdminID()); } catch(Exception e) {}
                    log.setAdminId(safeAdminId);
                    vn.edu.hmu.model.EmailAccount acc = dao.getAccountByStudentId(studentId);
                    log.setTargetEmail(acc != null ? acc.getEmailAddress() : studentId);
                    log.setActionType("SUSPEND");
                    log.setReason("Bảo lưu tài khoản thủ công. QĐ: " + decisionNumber);
                    log.setDetails("Bảo lưu thành công cho mã SV: " + studentId);
                    adminDAO.insertActionLog(log);
                    
                    vn.edu.hmu.dao.ArchiveDAO archiveDAO = new vn.edu.hmu.dao.ArchiveDAO();
                    archiveDAO.insertArchiveM02("SUSPEND", decisionNumber, 0,
                        acc != null ? acc.getStudentName() : "N/A",
                        acc != null ? acc.getEmailAddress() : "N/A",
                        studentId, "N/A", safeAdminId);
                }
            } catch(Exception ignored) {}

            out.print("{\"success\": true, \"message\": \"Khóa tài khoản thành công!\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Lỗi hệ thống: Không thể khóa tài khoản!\"}");
        }
        out.flush();
    }
}