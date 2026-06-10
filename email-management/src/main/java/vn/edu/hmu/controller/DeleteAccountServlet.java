package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/delete-account")
public class DeleteAccountServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String studentId = request.getParameter("studentId");
        PrintWriter out = response.getWriter();

        if (studentId == null || studentId.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Mã sinh viên không hợp lệ.\"}");
            out.flush();
            return;
        }

        StudentDAO dao = new StudentDAO();
        boolean success = dao.markPendingDelete(studentId);

        if (success) {
            out.print("{\"success\": true, \"message\": \"Đã đặt tài khoản vào trạng thái chờ xóa.\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Không thể thay đổi trạng thái xóa.\"}");
        }
        out.flush();
    }
}
