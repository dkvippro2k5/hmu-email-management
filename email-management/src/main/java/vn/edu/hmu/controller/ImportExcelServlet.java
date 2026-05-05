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
                    // ĐÃ FIX LỖI: Lấy dữ liệu an toàn kể cả khi Excel định dạng là Số hay Chữ
                    String studentId = getSafeString(row.getCell(0));
                    String fullName = getSafeString(row.getCell(1));
                    String className = getSafeString(row.getCell(2));
                    String department = getSafeString(row.getCell(3));
                    String cohort = getSafeString(row.getCell(4));
                    String personalEmail = getSafeString(row.getCell(5));

                    // Nếu dòng trống mã SV thì bỏ qua
                    if (studentId.isEmpty()) continue;

                    Student student = new Student(studentId, fullName, className, department, cohort, personalEmail);

                    // FR-01.2: Tự động khởi tạo Email & Mật khẩu
                    String newEmail = AccountGenerator.generateEmail(fullName, studentId);
                    String newPassword = AccountGenerator.generateDefaultPassword();

                    // FR-01.3: Tính ngày kích hoạt (Hôm nay + 1 ngày)
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, 1);
                    Date activationDate = new Date(cal.getTimeInMillis());

                    EmailAccount emailAcc = new EmailAccount(newEmail, studentId, newPassword, 0, activationDate, null);

                    // Lưu xuống Database
                    boolean isSaved = studentDAO.importStudentAndEmail(student, emailAcc);
                    if (isSaved) successCount++;

                    final String targetEmail = personalEmail; // Biến phải là final để dùng trong lambda
                    final String targetName = fullName;
                    final String hmuEmail = newEmail;
                    final String defaultPass = newPassword;
                    final int threadDelay = successCount * 1000; // Delay tăng dần theo số lượng email đã gửi (giả lập hàng đợi)
                    
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(threadDelay); // Giả lập delay để tránh gửi email hàng loạt cùng lúc
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
                    System.out.println("Lỗi ở dòng " + i + ": " + ex.getMessage());
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
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return cell.getStringCellValue().trim();
    }
}