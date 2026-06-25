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

@WebServlet("/activate-account")
public class ActivateAccountServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String newPass = request.getParameter("newPassword");
        String confirmPass = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        
        HttpSession session = request.getSession();
        EmailAccount acc = (EmailAccount) session.getAttribute("user");

        if (acc != null && newPass != null && newPass.equals(confirmPass)) {
            StudentDAO dao = new StudentDAO();
            
            // Hash mật khẩu Portal mới
            String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(newPass, org.mindrot.jbcrypt.BCrypt.gensalt());
            
            // 1. Cập nhật mật khẩu Portal
            boolean isUpdatedPw = dao.updatePortalPassword(acc.getStudentId(), passwordHash);
            
            // 2. Cập nhật số điện thoại
            boolean isUpdatedPhone = true;
            if (phone != null && !phone.trim().isEmpty()) {
                isUpdatedPhone = dao.updateStudentPhone(acc.getStudentId(), phone);
            }
            
            if (isUpdatedPw && isUpdatedPhone) {
                // Log action
                AdminDAO adminDAO = new AdminDAO();
                ActionLog log = new ActionLog();
                log.setActionType("FIRST_LOGIN_SETUP");
                log.setTargetEmail(acc.getEmailAddress());
                log.setReason("Sinh viên đổi mật khẩu Portal và cập nhật SĐT ở lần đăng nhập đầu tiên");
                log.setDetails("Tài khoản " + acc.getStudentId() + " đã thiết lập ban đầu.");
                adminDAO.insertActionLog(log);
                
                // 3. Chuyển sang bước xác nhận thông tin (Verify)
                response.sendRedirect("verify-info");
            } else {
                response.sendRedirect("first-login.jsp?error=system");
            }
        } else {
            response.sendRedirect("first-login.jsp?error=mismatch");
        }
    }
}