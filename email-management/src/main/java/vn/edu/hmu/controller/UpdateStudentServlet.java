package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/update-student")
public class UpdateStudentServlet extends HttpServlet {
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
        String emailAddress = request.getParameter("emailAddress");
        String statusRaw = request.getParameter("status");

        int status = 1;
        try {
            status = Integer.parseInt(statusRaw);
        } catch (NumberFormatException ignored) {}

        PrintWriter out = response.getWriter();

        if (studentId == null || studentId.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Mã sinh viên không được để trống!\"}");
            out.flush();
            return;
        }

        Student student = new Student();
        student.setFullName(fullName);
        student.setGender(gender);
        student.setDateOfBirth(dateOfBirth);
        student.setClassName(className);
        student.setDepartment(department);
        student.setMajor(major);
        student.setCohort(cohort);
        student.setPersonalEmail(personalEmail);

        StudentDAO dao = new StudentDAO();
        boolean success = dao.updateStudentInfo(studentId, student, emailAddress, status);

        if (success) {
            out.print("{\"success\": true, \"message\": \"Cập nhật thông tin sinh viên thành công.\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Lỗi khi lưu thông tin sinh viên.\"}");
        }
        out.flush();
    }
}
