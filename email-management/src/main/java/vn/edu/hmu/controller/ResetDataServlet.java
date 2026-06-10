package vn.edu.hmu.controller;

import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.ITAdmin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/reset-data")
public class ResetDataServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        ITAdmin currentAdmin = (ITAdmin) session.getAttribute("currentAdmin");
        if (currentAdmin == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String adminPassword = request.getParameter("adminPassword");
        if (adminPassword == null || adminPassword.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Mật khẩu admin không được để trống.");
            response.sendRedirect("dashboard");
            return;
        }

        AdminDAO adminDAO = new AdminDAO();
        ITAdmin validatedAdmin = adminDAO.checkLogin(currentAdmin.getUsername(), adminPassword);
        if (validatedAdmin == null) {
            session.setAttribute("errorMsg", "Mật khẩu admin không đúng.");
            response.sendRedirect("dashboard");
            return;
        }

        StudentDAO studentDAO = new StudentDAO();
        String errorMessage = studentDAO.deleteAllStudentsAndEmails();

        if (errorMessage == null) {
            session.setAttribute("successMsg", "Đã reset toàn bộ dữ liệu sinh viên thành công.");
        } else {
            session.setAttribute("errorMsg", "Không thể reset dữ liệu. Vui lòng kiểm tra database. Lỗi: " + errorMessage);
        }
        response.sendRedirect("dashboard");
    }
}
