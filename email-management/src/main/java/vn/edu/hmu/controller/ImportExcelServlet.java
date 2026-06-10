package vn.edu.hmu.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.Student;
import vn.edu.hmu.util.AccountGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.InputStream;
import java.sql.Date;
import java.util.Calendar;

@WebServlet("/import-students") // Giữ nguyên link cũ của bạn
@MultipartConfig // Bắt buộc phải có để nhận file Upload
public class ImportExcelServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, java.io.IOException {
        
        Part filePart = request.getPart("excelFile"); 
        InputStream fileContent = filePart.getInputStream();
        StudentDAO studentDAO = new StudentDAO();
        int successCount = 0;

        try (Workbook workbook = new XSSFWorkbook(fileContent)) {
            Sheet sheet = workbook.getSheetAt(0); 

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // Cấu trúc file Excel mới (10 cột):
                    // 0: STT, 1: Mã SV, 2: Họ tên, 3: Giới tính, 4: Ngày sinh, 
                    // 5: Tên lớp, 6: Khoa, 7: Ngành học, 8: Niên khóa, 9: Email cá nhân
                    String studentId = getSafeString(row.getCell(1));
                    String fullName = getSafeString(row.getCell(2));
                    String gender = getSafeString(row.getCell(3));
                    String dateOfBirth = normalizeDate(getSafeString(row.getCell(4)));
                    String className = getSafeString(row.getCell(5));
                    String department = getSafeString(row.getCell(6));
                    String major = getSafeString(row.getCell(7));
                    String cohort = getSafeString(row.getCell(8));
                    String personalEmail = getSafeString(row.getCell(9));

                    if (studentId.isEmpty() || fullName.isEmpty()) {
                        continue; 
                    }

                    String lengthError = validateImportRow(studentId, fullName, gender, dateOfBirth, className, department, major, cohort, personalEmail);
                    if (lengthError != null) {
                        request.getSession().setAttribute("errorMsg", "Lỗi import tại dòng " + (i + 1) + ": " + lengthError);
                        response.sendRedirect("dashboard");
                        return;
                    }

                    Student student = new Student(studentId, fullName, gender, dateOfBirth, className, department, major, cohort, personalEmail);

                    String newEmail = AccountGenerator.generateEmail(fullName, studentId);
                    String newPassword = AccountGenerator.generateDefaultPassword();

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, 1);
                    Date activationDate = new Date(cal.getTimeInMillis());

                    EmailAccount emailAcc = new EmailAccount(newEmail, studentId, newPassword, 0, activationDate, null);

                    String importError = studentDAO.importStudentAndEmail(student, emailAcc);
                    if (importError != null) {
                        request.getSession().setAttribute("errorMsg", "Lỗi import tại dòng " + (i + 1) + ": " + importError);
                        response.sendRedirect("dashboard");
                        return;
                    }
                    successCount++;

                    final String targetEmail = personalEmail;
                    final String targetName = fullName;
                    final String hmuEmail = newEmail;
                    final String defaultPass = newPassword;
                    final int threadDelay = successCount * 1000;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(threadDelay);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            try {
                                vn.edu.hmu.util.EmailService.sendWelcomeEmail(targetEmail, targetName, hmuEmail, defaultPass);
                            } catch (Exception e) {
                                System.out.println("Lỗi gửi email đến " + targetEmail + ": " + e.getMessage());
                            }
                        }
                    }).start();

                } catch (Exception ex) {
                    request.getSession().setAttribute("errorMsg", "Lỗi đọc dòng " + (i + 1) + ": " + ex.getMessage());
                    response.sendRedirect("dashboard");
                    return;
                }
            }

            // Lưu câu thông báo vào Session (bộ nhớ tạm)
            request.getSession().setAttribute("successMsg", "✅ Đã import thành công " + successCount + " sinh viên!");
            
            // Chuyển hướng về lại dashboard theo đường link Servlet (thay vì foward thẳng vào jsp)
            response.sendRedirect("dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Lỗi đọc file Excel: " + e.getMessage());
        }
    }

    // Hàm hỗ trợ đọc Excel không bị lỗi sập app
    private String getSafeString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return new java.text.SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
            }
            double value = cell.getNumericCellValue();
            long longValue = (long) value;
            if (value == longValue) {
                return String.valueOf(longValue);
            }
            return String.valueOf(value);
        }
        if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        if (cell.getCellType() == CellType.FORMULA) {
            switch (cell.getCachedFormulaResultType()) {
                case STRING:
                    return cell.getRichStringCellValue().getString().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                    }
                    double value = cell.getNumericCellValue();
                    long longValue = (long) value;
                    if (value == longValue) {
                        return String.valueOf(longValue);
                    }
                    return String.valueOf(value);
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                default:
                    return "";
            }
        }
        return "";
    }

    private String normalizeDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return "";
        dateStr = dateStr.trim();
        // Nếu đã đúng định dạng yyyy-MM-dd thì trả về luôn
        if (dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) return dateStr;

        String[] formats = {"dd/MM/yyyy", "d/m/yyyy", "dd-MM-yyyy", "d-m-yyyy"};
        for (String format : formats) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
                sdf.setLenient(false);
                java.util.Date date = sdf.parse(dateStr);
                return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
            } catch (Exception ignored) {}
        }
        return dateStr;
    }

    private String validateImportRow(String studentId, String fullName, String gender,
                                     String dateOfBirth, String className, String department,
                                     String major, String cohort, String personalEmail) {
        if (studentId.length() > 20) return "Mã SV quá dài (tối đa 20 ký tự).";
        if (fullName.length() > 100) return "Họ tên quá dài (tối đa 100 ký tự).";
        if (gender.length() > 10) return "Giới tính quá dài (tối đa 10 ký tự).";
        if (dateOfBirth.length() > 15) return "Ngày sinh quá dài (tối đa 15 ký tự).";
        if (className.length() > 30) return "Lớp quá dài (tối đa 30 ký tự).";
        if (department.length() > 100) return "Khoa/Đơn vị quá dài (tối đa 100 ký tự).";
        if (major.length() > 100) return "Ngành học quá dài (tối đa 100 ký tự).";
        if (cohort.length() > 15) return "Niên khóa quá dài (tối đa 15 ký tự).";
        if (personalEmail.length() > 120) return "Email cá nhân quá dài (tối đa 120 ký tự).";
        return null;
    }
}