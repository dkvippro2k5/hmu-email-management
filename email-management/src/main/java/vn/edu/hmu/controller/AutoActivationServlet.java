package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.ActionLog;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(name = "AutoActivationServlet", loadOnStartup = 1) // Tự động khởi tạo khi server start
public class AutoActivationServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AutoActivationServlet.class.getName());
    private Timer timer;

    @Override
    public void init() throws ServletException {
        super.init();

        // Khởi tạo timer để chạy mỗi giờ (3600000 ms = 1 giờ)
        timer = new Timer(true); // Daemon thread
        timer.scheduleAtFixedRate(new AutoActivationTask(), 60000, 3600000); // Chạy sau 1 phút, lặp lại mỗi giờ

        logger.info("AutoActivationServlet initialized - Account auto-activation service started");
    }

    @Override
    public void destroy() {
        super.destroy();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        logger.info("AutoActivationServlet destroyed - Account auto-activation service stopped");
    }

    // Task để tự động kích hoạt tài khoản
    private class AutoActivationTask extends TimerTask {
        @Override
        public void run() {
            try {
                StudentDAO studentDAO = new StudentDAO();
                AdminDAO adminDAO = new AdminDAO();

                // Lấy danh sách tài khoản cần tự động kích hoạt
                List<EmailAccount> pendingAccounts = studentDAO.getAccountsPendingAutoActivation();

                if (!pendingAccounts.isEmpty()) {
                    logger.info("Found " + pendingAccounts.size() + " accounts pending auto-activation");

                    int activatedCount = 0;

                    for (EmailAccount account : pendingAccounts) {
                        // Tự động kích hoạt tài khoản
                        boolean success = studentDAO.autoActivateAccount(account.getEmailAddress());

                        if (success) {
                            activatedCount++;

                            // Ghi log cho hành động tự động kích hoạt
                            ActionLog log = new ActionLog(1, account.getEmailAddress(), "AUTO_ACTIVATE",
                                "Tự động kích hoạt tài khoản sau 24 giờ tạo tài khoản");
                            adminDAO.insertActionLog(log);

                            logger.info("Auto-activated account: " + account.getEmailAddress());
                        } else {
                            logger.warning("Failed to auto-activate account: " + account.getEmailAddress());
                        }
                    }

                    logger.info("Auto-activation completed: " + activatedCount + "/" + pendingAccounts.size() + " accounts activated");

                } else {
                    logger.fine("No accounts pending auto-activation");
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during auto-activation process", e);
            }
        }
    }
}