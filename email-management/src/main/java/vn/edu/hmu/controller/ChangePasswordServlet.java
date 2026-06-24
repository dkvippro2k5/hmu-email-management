package vn.edu.hmu.controller;

import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.ActionLog;
import vn.edu.hmu.model.EmailAccount;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        EmailAccount acc = (EmailAccount) session.getAttribute("user");

        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Xử lý nút "Để sau" (Skip)
        String skip = request.getParameter("skip");
        if ("true".equals(skip)) {
            if (acc.getStatus() == 0) {
                response.sendRedirect("verify-info");
            } else {
                response.sendRedirect("student-portal.jsp");
            }
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null || newPassword.length() < 8 || !newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu không hợp lệ hoặc không khớp.");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }

        StudentDAO studentDAO = new StudentDAO();
        // Hash mật khẩu mới bằng BCrypt
        String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(newPassword, org.mindrot.jbcrypt.BCrypt.gensalt());
        
        boolean success = studentDAO.updatePortalPassword(acc.getStudentId(), passwordHash);

        if (success) {
            // Log action
            AdminDAO adminDAO = new AdminDAO();
            ActionLog log = new ActionLog();
            log.setActionType("CHANGE_PORTAL_PASSWORD");
            log.setTargetEmail(acc.getEmailAddress());
            log.setReason("Sinh viên đổi mật khẩu Portal trong lần đăng nhập đầu tiên");
            log.setDetails("Tài khoản " + acc.getStudentId() + " đã cập nhật mật khẩu Portal.");
            adminDAO.insertActionLog(log);

            // Chuyển tiếp tới bước Kê khai thông tin hoặc Dashboard
            if (acc.getStatus() == 0) {
                response.sendRedirect("verify-info");
            } else {
                response.sendRedirect("student-portal.jsp");
            }
        } else {
            request.setAttribute("errorMessage", "Đã có lỗi xảy ra. Vui lòng thử lại!");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
        }
    }
}
