package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.Student;

import vn.edu.hmu.util.AccountGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;

@WebServlet("/create-student")
public class CreateStudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String studentId = request.getParameter("studentId");
        String fullName = request.getParameter("fullName");
        String cccd = request.getParameter("cccd");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName"); // Will keep reading parameter "lastName" from JS to avoid too many changes in frontend, rename internal variable
        String cohort = request.getParameter("cohort");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        PrintWriter out = response.getWriter();

        if (studentId == null || studentId.trim().isEmpty() || fullName == null || fullName.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Mã sinh viên, họ tên, email và mật khẩu là bắt buộc.\"}");
            out.flush();
            return;
        }

        Student student = new Student();
        student.setStudentId(studentId.trim());
        student.setFullName(fullName.trim());
        student.setCccd(cccd);
        student.setFirstName(firstName.trim());
        student.setLastName(lastName);
        student.setCohort(cohort);
        student.setPhoneNumber(phoneNumber);

        String portalPasswordHash = "";
        if (cccd != null && !cccd.trim().isEmpty()) {
            portalPasswordHash = org.mindrot.jbcrypt.BCrypt.hashpw(cccd.trim(), org.mindrot.jbcrypt.BCrypt.gensalt());
        }
        student.setPortalPassword(portalPasswordHash);

        String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(password.trim(), org.mindrot.jbcrypt.BCrypt.gensalt());
        String encryptedInitialPassword = vn.edu.hmu.util.AESUtil.encrypt(password.trim());

        EmailAccount emailAccount = new EmailAccount();
        emailAccount.setStudentId(student.getStudentId());
        emailAccount.setEmailAddress(email.trim());
        emailAccount.setPasswordHash(passwordHash);
        emailAccount.setInitialPasswordEncrypted(encryptedInitialPassword);
        emailAccount.setStatus(0); // Chờ kích hoạt
        emailAccount.setActivationDate(new java.sql.Date(System.currentTimeMillis()));

        StudentDAO dao = new StudentDAO();
        boolean success = dao.createStudentWithEmail(student, emailAccount);

        if (success) {
            try {
                vn.edu.hmu.model.ITAdmin admin = (vn.edu.hmu.model.ITAdmin) request.getSession().getAttribute("currentAdmin");
                if (admin != null) {
                    vn.edu.hmu.dao.AdminDAO adminDAO = new vn.edu.hmu.dao.AdminDAO();
                    vn.edu.hmu.model.ActionLog log = new vn.edu.hmu.model.ActionLog();
                    int safeAdminId = 1;
                    try { if (admin.getAdminID() != null) safeAdminId = Integer.parseInt(admin.getAdminID()); } catch(Exception e) {}
                    log.setAdminId(safeAdminId);
                    log.setTargetEmail(email.trim()); // Use actual email for FK constraint
                    log.setActionType("CREATE");
                    log.setReason("Tạo sinh viên thủ công");
                    log.setDetails("Email cấp: " + email.trim());
                    adminDAO.insertActionLog(log);
                }
            } catch(Exception ignored) {}

            out.print("{\"success\": true, \"message\": \"Thêm sinh viên mới thành công. Email: " + email.trim() + "\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Lỗi: Mã sinh viên đã tồn tại hoặc lỗi database.\"}");
        }
        out.flush();
    }
}
