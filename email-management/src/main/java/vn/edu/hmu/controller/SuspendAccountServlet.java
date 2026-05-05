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

        // 4. Gọi DAO cập nhật xuống Database
        StudentDAO dao = new StudentDAO();
        boolean isSuccess = dao.suspendAccount(studentId, decisionNumber);
        
        // 5. Trả kết quả về cho Frontend
        if (isSuccess) {
            out.print("{\"success\": true, \"message\": \"Khóa tài khoản thành công!\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Lỗi hệ thống: Không thể khóa tài khoản!\"}");
        }
        out.flush();
    }
}