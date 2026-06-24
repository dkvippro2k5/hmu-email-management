package vn.edu.hmu.controller;

import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.ActionLog;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/verify-info")
public class VerifyInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        EmailAccount acc = (EmailAccount) session.getAttribute("user");

        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy thông tin sinh viên từ database để hiển thị
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentById(acc.getStudentId());

        if (student == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        request.setAttribute("student", student);
        request.getRequestDispatcher("verify-info.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        EmailAccount acc = (EmailAccount) session.getAttribute("user");

        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Xử lý xác nhận
        StudentDAO studentDAO = new StudentDAO();
        boolean success = studentDAO.activateAccount(acc.getEmailAddress(), acc.getPasswordHash(), null);

        if (success) {
            // Log action
            AdminDAO adminDAO = new AdminDAO();
            ActionLog log = new ActionLog();
            log.setActionType("ACTIVATE_ACCOUNT");
            log.setTargetEmail(acc.getEmailAddress());
            log.setReason("Sinh viên tự xác nhận thông tin kích hoạt");
            log.setDetails("Tài khoản " + acc.getEmailAddress() + " đã được kích hoạt thành công.");
            adminDAO.insertActionLog(log);

            // Cập nhật session
            acc.setStatus(1);
            session.setAttribute("user", acc);

            response.sendRedirect("activation-success.jsp");
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi xác nhận kích hoạt. Vui lòng thử lại!");
            doGet(request, response);
        }
    }
}
