package vn.edu.hmu.controller;

import vn.edu.hmu.dao.SupportRequestDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.ITAdmin;
import vn.edu.hmu.model.SupportRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/support")
public class SupportServlet extends HttpServlet {

    private SupportRequestDAO supportDAO = new SupportRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("support.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        EmailAccount student = (EmailAccount) session.getAttribute("user");
        ITAdmin admin = (ITAdmin) session.getAttribute("currentAdmin");

        if (student == null && admin == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String senderId = (student != null) ? student.getStudentId() : admin.getAdminID();
        String subject = request.getParameter("subject");
        String content = request.getParameter("content");

        SupportRequest sr = new SupportRequest(senderId, subject, content);
        boolean success = supportDAO.insertRequest(sr);

        if (success) {
            request.setAttribute("message", "Yêu cầu của bạn đã được gửi thành công!");
        } else {
            request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau.");
        }
        request.getRequestDispatcher("support.jsp").forward(request, response);
    }
}
