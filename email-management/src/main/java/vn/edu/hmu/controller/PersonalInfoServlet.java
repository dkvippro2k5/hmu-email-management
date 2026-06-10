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
import java.util.List;

@WebServlet("/personal-info")
public class PersonalInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        EmailAccount acc = (EmailAccount) session.getAttribute("user");
        
        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        StudentDAO dao = new StudentDAO();
        AdminDAO adminDao = new AdminDAO();
        
        Student student = dao.getStudentById(acc.getStudentId());
        List<ActionLog> studentLogs = adminDao.getLogsByEmail(acc.getEmailAddress(), 5);
        
        request.setAttribute("student", student);
        request.setAttribute("studentLogs", studentLogs);
        request.getRequestDispatcher("personal-info.jsp").forward(request, response);
    }
}
