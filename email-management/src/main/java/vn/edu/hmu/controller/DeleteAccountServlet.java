package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.dao.ArchiveDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.util.EmailService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/delete-account")
public class DeleteAccountServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String studentId = request.getParameter("studentId");
        String decisionNumber = request.getParameter("decisionNumber");
        PrintWriter out = response.getWriter();

        if (studentId == null || studentId.trim().isEmpty() || decisionNumber == null || decisionNumber.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Mã sinh viên hoặc Số quyết định không hợp lệ.\"}");
            out.flush();
            return;
        }

        String customNotification = request.getParameter("customNotification");

        StudentDAO dao = new StudentDAO();
        boolean success = dao.markPendingDelete(studentId, decisionNumber);

        if (success) {
            // Lấy thông tin tài khoản để gửi mail cảnh báo
            EmailAccount acc = dao.getAccountByStudentId(studentId);
            if (acc != null) {
                if (customNotification != null && !customNotification.trim().isEmpty()) {
                    vn.edu.hmu.model.Notification notif = new vn.edu.hmu.model.Notification();
                    notif.setStudentId(acc.getStudentId());
                    notif.setTitle("THÔNG BÁO TỪ PHÒNG IT");
                    notif.setMessage(customNotification);
                    notif.setCreatedAt(new java.util.Date());
                    new vn.edu.hmu.dao.NotificationDAO().insertNotification(notif);
                }

                String toEmail = acc.getEmailAddress();
                String mailContent = EmailService.sendRevokeWarningEmail(toEmail, acc.getStudentName());
                if (mailContent != null) {
                    ArchiveDAO archiveDAO = new ArchiveDAO();
                    archiveDAO.insertArchivePL01(toEmail, acc.getStudentName(), decisionNumber, mailContent);
                }
            }

            try {
                vn.edu.hmu.model.ITAdmin admin = (vn.edu.hmu.model.ITAdmin) request.getSession().getAttribute("currentAdmin");
                if (admin != null) {
                    vn.edu.hmu.dao.AdminDAO adminDAO = new vn.edu.hmu.dao.AdminDAO();
                    vn.edu.hmu.model.ActionLog log = new vn.edu.hmu.model.ActionLog();
                    int safeAdminId = 1;
                    try { if (admin.getAdminID() != null) safeAdminId = Integer.parseInt(admin.getAdminID()); } catch(Exception e) {}
                    log.setAdminId(safeAdminId);
                    log.setTargetEmail(acc != null ? acc.getEmailAddress() : studentId);
                    log.setActionType("DELETE");
                    log.setReason("Thu hồi tài khoản thủ công. QĐ: " + decisionNumber);
                    log.setDetails("Đã đưa vào trạng thái chờ xóa. Mã SV: " + studentId);
                    adminDAO.insertActionLog(log);
                    
                    ArchiveDAO archiveDAO = new ArchiveDAO();
                    archiveDAO.insertArchiveM02("REVOKE", decisionNumber, 0,
                        acc != null ? acc.getStudentName() : "N/A",
                        acc != null ? acc.getEmailAddress() : "N/A",
                        studentId, "N/A", safeAdminId);
                }
            } catch(Exception ignored) {}

            out.print("{\"success\": true, \"message\": \"Đã đặt tài khoản vào trạng thái chờ xóa và gửi thông báo.\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Không thể thay đổi trạng thái xóa.\"}");
        }
        out.flush();
    }
}
