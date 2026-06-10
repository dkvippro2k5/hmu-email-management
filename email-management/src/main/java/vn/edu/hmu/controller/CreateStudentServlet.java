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
        String gender = request.getParameter("gender");
        String dateOfBirth = request.getParameter("dateOfBirth");
        String className = request.getParameter("className");
        String department = request.getParameter("department");
        String major = request.getParameter("major");
        String cohort = request.getParameter("cohort");
        String personalEmail = request.getParameter("personalEmail");

        PrintWriter out = response.getWriter();

        if (studentId == null || studentId.trim().isEmpty() || fullName == null || fullName.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Mã sinh viên và họ tên là bắt buộc.\"}");
            out.flush();
            return;
        }

        Student student = new Student();
        student.setStudentId(studentId.trim());
        student.setFullName(fullName.trim());
        student.setGender(gender);
        student.setDateOfBirth(dateOfBirth);
        student.setClassName(className);
        student.setDepartment(department);
        student.setMajor(major);
        student.setCohort(cohort);
        student.setPersonalEmail(personalEmail);

        // Sử dụng AccountGenerator để tạo Email và Mật khẩu đồng bộ với Import
        String generatedEmail = AccountGenerator.generateEmail(fullName, studentId);
        String defaultPassword = AccountGenerator.generateDefaultPassword();

        EmailAccount emailAccount = new EmailAccount();
        emailAccount.setStudentId(student.getStudentId());
        emailAccount.setEmailAddress(generatedEmail);
        emailAccount.setPasswordHash(defaultPassword); // Hash nếu cần, hiện tại đang lưu plain/default
        emailAccount.setStatus(0); // Chờ kích hoạt
        emailAccount.setActivationDate(new Date(System.currentTimeMillis()));

        StudentDAO dao = new StudentDAO();
        boolean success = dao.createStudentWithEmail(student, emailAccount);

        if (success) {
            out.print("{\"success\": true, \"message\": \"Thêm sinh viên mới thành công. Email: " + generatedEmail + "\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Lỗi: Mã sinh viên đã tồn tại hoặc lỗi database.\"}");
        }
        out.flush();
    }
}
