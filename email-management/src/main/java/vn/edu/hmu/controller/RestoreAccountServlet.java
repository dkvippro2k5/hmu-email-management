package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/restore-account")
public class RestoreAccountServlet extends HttpServlet {
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
        boolean success = dao.restoreAccount(studentId);

        if (success) {
            out.print("{\"success\": true, \"message\": \"Khôi phục tài khoản thành công.\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Không thể khôi phục tài khoản.\"}");
        }
        out.flush();
    }
}
