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
import java.io.IOException;
import java.util.List;

@WebServlet("/send-notification")
public class SendNotificationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        
        // Nhận bộ lọc nâng cao từ form
        String statusFilter = request.getParameter("status");
        String className = request.getParameter("className");
        String department = request.getParameter("department");
        String major = request.getParameter("major");
        String cohort = request.getParameter("cohort");

        int status = -1;
        try {
            if (statusFilter != null && !statusFilter.isEmpty()) {
                status = Integer.parseInt(statusFilter);
            }
        } catch (NumberFormatException ignored) {}

        // Tận dụng hàm tìm kiếm nâng cao hiện có (truyền keyword là rỗng)
        StudentDAO dao = new StudentDAO();
        List<EmailAccount> targetAccounts = dao.searchAccountsAdvanced("", status, className, department, major, cohort);

        // Mô phỏng quá trình gửi thông báo (Lưu vào ActionLog) và Thêm vào Notification table
        AdminDAO adminDAO = new AdminDAO();
        vn.edu.hmu.dao.NotificationDAO notifDAO = new vn.edu.hmu.dao.NotificationDAO();
        int sentCount = 0;
        
        if (targetAccounts != null && !targetAccounts.isEmpty()) {
            for (EmailAccount acc : targetAccounts) {
                // Thêm vào ActionLog
                ActionLog log = new ActionLog(1, acc.getEmailAddress(), "SEND_EMAIL", "Tiêu đề: " + subject + " - Gửi tới đối tượng có lọc");
                adminDAO.insertActionLog(log);
                
                // Thêm vào Notifications
                vn.edu.hmu.model.Notification n = new vn.edu.hmu.model.Notification(acc.getStudentId(), subject, message);
                notifDAO.insertNotification(n);
                
                sentCount++;
            }
            
            // Set session message
            request.getSession().setAttribute("successMsg", "Đã gửi thông báo thành công tới " + sentCount + " sinh viên.");
        } else {
            // Không tìm thấy tài khoản phù hợp
            request.getSession().setAttribute("errorMsg", "Không tìm thấy sinh viên nào khớp với bộ lọc để gửi thông báo.");
        }

        // Chuyển hướng về tab notify
        response.sendRedirect("dashboard#page-notify");
    }
}
