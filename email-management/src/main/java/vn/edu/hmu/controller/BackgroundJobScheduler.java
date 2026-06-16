package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.ActionLog;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(name = "BackgroundJobScheduler", value = "/background-jobs", loadOnStartup = 1)
public class BackgroundJobScheduler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(BackgroundJobScheduler.class.getName());
    private Timer timer;

    @Override
    public void init() throws ServletException {
        super.init();

        // Khởi tạo timer để chạy mỗi giờ (3600000 ms = 1 giờ)
        timer = new Timer(true); // Daemon thread
        
        // Task 1: Tự động kích hoạt (chạy sau 1 phút, lặp lại mỗi giờ)
        timer.scheduleAtFixedRate(new AutoActivationTask(), 60000, 3600000); 
        
        // Task 2: Tự động xóa vĩnh viễn tài khoản hết hạn chờ xóa (chạy sau 2 phút, lặp lại mỗi giờ)
        timer.scheduleAtFixedRate(new AutoDeleteTask(), 120000, 3600000);

        logger.info("BackgroundJobScheduler initialized - All automated services started.");
    }

    @Override
    public void destroy() {
        super.destroy();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        logger.info("BackgroundJobScheduler destroyed - All automated services stopped.");
    }

    // Task 1: Tự động kích hoạt tài khoản
    private class AutoActivationTask extends TimerTask {
        @Override
        public void run() {
            try {
                StudentDAO studentDAO = new StudentDAO();
                AdminDAO adminDAO = new AdminDAO();

                List<EmailAccount> pendingAccounts = studentDAO.getAccountsPendingAutoActivation();

                if (!pendingAccounts.isEmpty()) {
                    logger.info("Found " + pendingAccounts.size() + " accounts pending auto-activation");
                    int activatedCount = 0;

                    for (EmailAccount account : pendingAccounts) {
                        boolean success = studentDAO.autoActivateAccount(account.getEmailAddress());
                        if (success) {
                            activatedCount++;
                            ActionLog log = new ActionLog(1, account.getEmailAddress(), "AUTO_ACTIVATE",
                                "Tự động kích hoạt tài khoản sau 24 giờ tạo tài khoản");
                            adminDAO.insertActionLog(log);
                        }
                    }
                    logger.info("Auto-activation completed: " + activatedCount + "/" + pendingAccounts.size() + " accounts activated");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during auto-activation process", e);
            }
        }
    }

    // Task 2: Tự động xóa vĩnh viễn tài khoản đã hết thời hạn chờ 30 ngày
    private class AutoDeleteTask extends TimerTask {
        @Override
        public void run() {
            try {
                StudentDAO studentDAO = new StudentDAO();
                AdminDAO adminDAO = new AdminDAO();

                List<EmailAccount> expiredAccounts = studentDAO.getAccountsPendingAutoDelete();

                if (!expiredAccounts.isEmpty()) {
                    logger.info("Found " + expiredAccounts.size() + " accounts pending auto-deletion (expired the 30-day wait)");
                    int deletedCount = 0;

                    for (EmailAccount account : expiredAccounts) {
                        boolean success = studentDAO.permanentlyDeleteAccount(account.getStudentId());
                        if (success) {
                            deletedCount++;
                            ActionLog log = new ActionLog(1, account.getEmailAddress(), "AUTO_DELETE",
                                "Tự động xóa vĩnh viễn tài khoản do hết hạn 30 ngày chờ thu hồi.");
                            adminDAO.insertActionLog(log);
                        }
                    }
                    logger.info("Auto-deletion completed: " + deletedCount + "/" + expiredAccounts.size() + " accounts permanently deleted.");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during auto-deletion process", e);
            }
        }
    }
}
