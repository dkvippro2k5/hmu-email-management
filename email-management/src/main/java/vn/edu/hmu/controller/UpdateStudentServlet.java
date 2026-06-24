package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.Student;
import vn.edu.hmu.model.ITAdmin;
import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.model.ActionLog;

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
        String cccd = request.getParameter("cccd");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName"); // The parameter is mapped to "lastName" in JS for now, keep parsing it as lastName or rename it later, but let's rename the variable to lastName.
        String cohort = request.getParameter("cohort");
        String phoneNumber = request.getParameter("phoneNumber");
        
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
        student.setCccd(cccd);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setCohort(cohort);
        student.setPhoneNumber(phoneNumber);

        StudentDAO dao = new StudentDAO();
        boolean success = dao.updateStudentInfo(studentId, student, emailAddress, status);

        if (success) {
            // Log action
            ITAdmin currentAdmin = (ITAdmin) request.getSession().getAttribute("currentAdmin");
            if (currentAdmin != null) {
                try {
                    int safeAdminId = 1;
                    try { if (currentAdmin.getAdminID() != null) safeAdminId = Integer.parseInt(currentAdmin.getAdminID()); } catch(Exception e) {}
                    AdminDAO adminDAO = new AdminDAO();
                    ActionLog log = new ActionLog();
                    log.setAdminId(safeAdminId);
                    log.setTargetEmail(emailAddress);
                    log.setActionType("EDIT");
                    log.setReason("Cập nhật thông tin sinh viên/tài khoản");
                    String details = String.format("{\"studentId\": \"%s\", \"fullName\": \"%s\", \"status\": %d}", studentId, fullName, status);
                    log.setDetails(details);
                    adminDAO.insertActionLog(log);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            out.print("{\"success\": true, \"message\": \"Cập nhật thông tin sinh viên thành công.\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Lỗi khi lưu thông tin sinh viên.\"}");
        }
        out.flush();
    }
}
