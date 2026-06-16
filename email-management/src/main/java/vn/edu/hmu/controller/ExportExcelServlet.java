package vn.edu.hmu.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/export-excel")
public class ExportExcelServlet extends HttpServlet {
    private StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String[] statusParams = request.getParameterValues("status");
        List<Integer> statuses = new ArrayList<>();
        if (statusParams != null) {
            for (String s : statusParams) {
                try {
                    statuses.add(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        
        String department = request.getParameter("department");
        String major = request.getParameter("major");
        String studentClass = request.getParameter("studentClass");
        String cohort = request.getParameter("cohort");

        List<EmailAccount> accounts = studentDAO.exportAccountsAdvanced(statuses, studentClass, department, major, cohort);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"Danh_sach_tai_khoan.xlsx\"");

        try (Workbook workbook = new XSSFWorkbook(); OutputStream out = response.getOutputStream()) {
            Sheet sheet = workbook.createSheet("Danh sách sinh viên");
            
            // Header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "Họ và tên", "Mã SV", "Giới tính", "Ngày sinh", "Lớp", "Khoa", "Ngành học", "Khóa", "Email cá nhân", "Email được cấp", "Trạng thái"};
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            int rowIdx = 1;
            for (EmailAccount acc : accounts) {
                Row row = sheet.createRow(rowIdx++);
                
                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(acc.getStudentName() != null ? acc.getStudentName() : "");
                row.createCell(2).setCellValue(acc.getStudentId() != null ? acc.getStudentId() : "");
                row.createCell(3).setCellValue(acc.getGender() != null ? acc.getGender() : "");
                row.createCell(4).setCellValue(acc.getDateOfBirth() != null ? acc.getDateOfBirth() : "");
                row.createCell(5).setCellValue(acc.getClassName() != null ? acc.getClassName() : "");
                row.createCell(6).setCellValue(acc.getDepartment() != null ? acc.getDepartment() : "");
                row.createCell(7).setCellValue(acc.getMajor() != null ? acc.getMajor() : "");
                row.createCell(8).setCellValue(acc.getCohort() != null ? acc.getCohort() : "");
                row.createCell(9).setCellValue(acc.getPersonalEmail() != null ? acc.getPersonalEmail() : "");
                row.createCell(10).setCellValue(acc.getEmailAddress() != null ? acc.getEmailAddress() : "");
                
                String statusText = "Không xác định";
                switch (acc.getStatus()) {
                    case 0: statusText = "Chờ kích hoạt"; break;
                    case 1: statusText = "Hoạt động"; break;
                    case 2: statusText = "Đang bảo lưu"; break;
                    case 3: statusText = "Chờ xóa"; break;
                }
                row.createCell(11).setCellValue(statusText);
            }
            
            // Auto size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
