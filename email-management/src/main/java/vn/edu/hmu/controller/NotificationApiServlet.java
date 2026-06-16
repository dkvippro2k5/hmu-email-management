package vn.edu.hmu.controller;

import com.google.gson.Gson;
import vn.edu.hmu.dao.NotificationDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.Notification;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/notifications")
public class NotificationApiServlet extends HttpServlet {
    private NotificationDAO notificationDAO = new NotificationDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(401);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }

        EmailAccount user = (EmailAccount) session.getAttribute("user");
        List<Notification> list = notificationDAO.getNotificationsForStudent(user.getStudentId());
        int unreadCount = notificationDAO.countUnread(user.getStudentId());

        System.out.println("NotificationApiServlet - Fetching for student: " + user.getStudentId());
        System.out.println("Notifications found: " + list.size());

        Map<String, Object> result = new HashMap<>();
        result.put("notifications", list);
        result.put("unreadCount", unreadCount);

        response.getWriter().write(gson.toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(401);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }

        String action = request.getParameter("action");
        if ("markRead".equals(action)) {
            String idStr = request.getParameter("id");
            try {
                int id = Integer.parseInt(idStr);
                notificationDAO.markAsRead(id);
                response.getWriter().write("{\"success\": true}");
            } catch (NumberFormatException e) {
                response.setStatus(400);
                response.getWriter().write("{\"error\": \"Invalid ID\"}");
            }
        } else {
            response.setStatus(400);
            response.getWriter().write("{\"error\": \"Invalid action\"}");
        }
    }
}
