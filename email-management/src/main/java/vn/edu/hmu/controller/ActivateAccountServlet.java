package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.EmailAccount;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/activate-account")
public class ActivateAccountServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String newPass = request.getParameter("newPassword");
        String confirmPass = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        
        HttpSession session = request.getSession();
        EmailAccount acc = (EmailAccount) session.getAttribute("user");

        if (acc != null && newPass.equals(confirmPass)) {
            StudentDAO dao = new StudentDAO();
            
            // 1. Cập nhật mật khẩu mới và đổi Status lên 1 (Active)
            boolean isUpdated = dao.activateAccount(acc.getEmailAddress(), newPass, phone);
            
            if (isUpdated) {
                // 2. Cập nhật lại đối tượng trong session
                acc.setStatus(1);
                session.setAttribute("user", acc);
                
                // 3. Cho phép vào Dashboard
                response.sendRedirect("dashboard");
            } else {
                response.sendRedirect("first-login.jsp?error=system");
            }
        } else {
            response.sendRedirect("first-login.jsp?error=mismatch");
        }
    }
}