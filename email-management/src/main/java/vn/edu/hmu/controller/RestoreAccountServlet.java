package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/restore-account")
public class RestoreAccountServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String studentId = request.getParameter("studentId");
        PrintWriter out = response.getWriter();

        if (studentId == null || studentId.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Mã sinh viên không hợp lệ.\"}");
            out.flush();
            return;
        }

        StudentDAO dao = new StudentDAO();
        boolean success = dao.restoreAccount(studentId);

        if (success) {
            try {
                vn.edu.hmu.model.EmailAccount acc = dao.getAccountByStudentId(studentId);
                if (acc != null) {
                    vn.edu.hmu.dao.ArchiveDAO archiveDAO = new vn.edu.hmu.dao.ArchiveDAO();
                    String toEmail = acc.getPersonalEmail() != null ? acc.getPersonalEmail() : acc.getEmailAddress();
                    String mailContent = "Tài khoản email của bạn đã được khôi phục thành công. Bạn có thể đăng nhập bình thường.";
                    archiveDAO.insertArchivePL01(toEmail, acc.getStudentName(), "THÔNG BÁO: Khôi phục tài khoản", mailContent);
                }

                vn.edu.hmu.model.ITAdmin admin = (vn.edu.hmu.model.ITAdmin) request.getSession().getAttribute("currentAdmin");
                if (admin != null) {
                    vn.edu.hmu.dao.AdminDAO adminDAO = new vn.edu.hmu.dao.AdminDAO();
                    vn.edu.hmu.model.ActionLog log = new vn.edu.hmu.model.ActionLog();
                    int safeAdminId = 1;
                    try { if (admin.getAdminID() != null) safeAdminId = Integer.parseInt(admin.getAdminID()); } catch(Exception e) {}
                    log.setAdminId(safeAdminId);
                    log.setTargetEmail(acc != null ? acc.getEmailAddress() : studentId);
                    log.setActionType("RESTORE");
                    log.setReason("Khôi phục tài khoản thủ công");
                    log.setDetails("Khôi phục thành công. Mã SV: " + studentId);
                    adminDAO.insertActionLog(log);
                }
            } catch(Exception ignored) {}

            out.print("{\"success\": true, \"message\": \"Khôi phục tài khoản thành công.\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Không thể khôi phục tài khoản.\"}");
        }
        out.flush();
    }
}
