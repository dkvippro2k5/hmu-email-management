package vn.edu.hmu.controller;

import vn.edu.hmu.dao.SupportRequestDAO;
import vn.edu.hmu.model.SupportRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin-support")
public class AdminSupportServlet extends HttpServlet {
    private SupportRequestDAO supportDAO = new SupportRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("currentAdmin") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("resolve".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            supportDAO.updateStatus(id, 1);
            response.sendRedirect("dashboard");
            return;
        }

        List<SupportRequest> list = supportDAO.getAllRequests();
        request.setAttribute("supportRequests", list);
        request.getRequestDispatcher("admin-support.jsp").forward(request, response);
    }
}
