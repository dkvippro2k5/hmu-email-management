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

        PrintWriter out = response.getWriter();

        if (studentId == null || studentId.trim().isEmpty() || fullName == null || fullName.trim().isEmpty() || firstName == null || firstName.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Mã sinh viên, họ tên và tên là bắt buộc.\"}");
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

        // Sử dụng AccountGenerator để tạo Email và Mật khẩu đồng bộ với Import
        String generatedEmail = AccountGenerator.generateEmail(firstName.trim(), lastName != null ? lastName : "", studentId.trim());
        String rawPassword = AccountGenerator.generateDefaultPassword();
        String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt());

        EmailAccount emailAccount = new EmailAccount();
        emailAccount.setStudentId(student.getStudentId());
        emailAccount.setEmailAddress(generatedEmail);
        emailAccount.setPasswordHash(passwordHash); 
        emailAccount.setStatus(0); // Chờ kích hoạt
        emailAccount.setActivationDate(new Date(System.currentTimeMillis()));

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
                    log.setTargetEmail(generatedEmail); // Use actual email for FK constraint
                    log.setActionType("CREATE");
                    log.setReason("Tạo sinh viên thủ công");
                    log.setDetails("Email cấp: " + generatedEmail);
                    adminDAO.insertActionLog(log);
                }
            } catch(Exception ignored) {}

            out.print("{\"success\": true, \"message\": \"Thêm sinh viên mới thành công. Email: " + generatedEmail + "\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Lỗi: Mã sinh viên đã tồn tại hoặc lỗi database.\"}");
        }
        out.flush();
    }
}
