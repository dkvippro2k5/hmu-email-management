package vn.edu.hmu.controller;

import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.ActionLog;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.util.AccountGenerator;
import vn.edu.hmu.util.EmailService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();
    private AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");

        if (email == null || email.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập địa chỉ email trường.");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            return;
        }

        EmailAccount acc = studentDAO.getAccountByEmail(email);

        if (acc == null) {
            request.setAttribute("error", "Không tìm thấy tài khoản với email này.");
        } else {
            String newPassword = AccountGenerator.generateDefaultPassword();
            boolean success = studentDAO.updatePassword(email, newPassword);

            if (success) {
                // Log action for Admin (adminId = 0 for System)
                ActionLog log = new ActionLog(0, email, "RESET_PASSWORD", "Hệ thống tự động cấp lại mật khẩu theo yêu cầu của sinh viên.");
                adminDAO.insertActionLog(log);

                // Send email to personal email
                EmailService.sendForgotPasswordEmail(acc.getPersonalEmail(), acc.getStudentName(), newPassword);

                request.setAttribute("message", "Mật khẩu mới đã được gửi tới email cá nhân của bạn (" + maskEmail(acc.getPersonalEmail()) + ").");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi cập nhật mật khẩu. Vui lòng thử lại sau.");
            }
        }
        request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String name = parts[0];
        if (name.length() <= 2) return "*" + "@" + parts[1];
        return name.substring(0, 2) + "****" + "@" + parts[1];
    }
}
