package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.ActionLog;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/test-auto-activation")
public class TestAutoActivationServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TestAutoActivationServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Chỉ cho phép admin truy cập
        if (request.getSession().getAttribute("currentAdmin") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        try {
            StudentDAO studentDAO = new StudentDAO();
            AdminDAO adminDAO = new AdminDAO();

            // Lấy danh sách tài khoản cần tự động kích hoạt
            List<EmailAccount> pendingAccounts = studentDAO.getAccountsPendingAutoActivation();

            int activatedCount = 0;

            for (EmailAccount account : pendingAccounts) {
                // Tự động kích hoạt tài khoản
                boolean success = studentDAO.autoActivateAccount(account.getEmailAddress());

                if (success) {
                    activatedCount++;

                    // Ghi log cho hành động tự động kích hoạt
                    ActionLog log = new ActionLog(1, account.getEmailAddress(), "AUTO_ACTIVATE",
                        "Tự động kích hoạt tài khoản sau 24 giờ tạo tài khoản (manual trigger)");
                    adminDAO.insertActionLog(log);

                    logger.info("Manually auto-activated account: " + account.getEmailAddress());
                }
            }

            // Trả về kết quả JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.format(
                "{\"success\": true, \"message\": \"Đã kích hoạt %d/%d tài khoản\", \"activated\": %d, \"total\": %d}",
                activatedCount, pendingAccounts.size(), activatedCount, pendingAccounts.size()
            ));

        } catch (Exception e) {
            logger.severe("Error in manual auto-activation: " + e.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi: " + e.getMessage() + "\"}");
        }
    }
}