package vn.edu.hmu.dao;

import vn.edu.hmu.model.Student;
import vn.edu.hmu.util.DBConnection;
import vn.edu.hmu.model.EmailAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean importStudentAndEmail(Student student, EmailAccount emailAcc) {
        String sqlStudent = "INSERT INTO students (student_id, full_name, class_name, department, cohort, personal_email) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEmail = "INSERT INTO email_accounts (email_address, student_id, password_hash, status, activation_date) VALUES (?, ?, ?, ?, ?)";
        
        // DBConnection là class kết nối Database của bạn
        try (Connection conn = DBConnection.getConnection()) {
            // Tắt tự động lưu để quản lý Transaction
            conn.setAutoCommit(false); 

            try (PreparedStatement ps1 = conn.prepareStatement(sqlStudent);
                 PreparedStatement ps2 = conn.prepareStatement(sqlEmail)) {
                
                // 1. Lưu thông tin Sinh viên
                ps1.setString(1, student.getStudentId());
                ps1.setString(2, student.getFullName());
                ps1.setString(3, student.getClassName());
                ps1.setString(4, student.getDepartment());
                ps1.setString(5, student.getCohort());
                ps1.setString(6, student.getPersonalEmail());
                ps1.executeUpdate();

                // 2. Lưu thông tin Email vừa sinh ra
                ps2.setString(1, emailAcc.getEmailAddress());
                ps2.setString(2, emailAcc.getStudentId());
                ps2.setString(3, emailAcc.getPasswordHash());
                ps2.setInt(4, emailAcc.getStatus());
                ps2.setDate(5, emailAcc.getActivationDate());
                ps2.executeUpdate();

                // Xác nhận lưu toàn bộ thành công
                conn.commit(); 
                return true;
                
            } catch (SQLException e) {
                conn.rollback(); // Nếu có lỗi bất kỳ, hủy bỏ toàn bộ thao tác
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<EmailAccount> getAllAccounts() {
        List<EmailAccount> accountList = new ArrayList<>();
        // Câu lệnh SQL lấy dữ liệu (bạn có thể JOIN với bảng students nếu muốn lấy cả Họ Tên)
        String sql = "SELECT e.email_address, e.student_id, s.full_name, e.status, e.activation_date " +
                     "FROM email_accounts e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "ORDER BY e.activation_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Vòng lặp: Cứ mỗi dòng trong MySQL, tạo ra một Object EmailAccount
            while (rs.next()) {
                EmailAccount acc = new EmailAccount();
                acc.setEmailAddress(rs.getString("email_address"));
                acc.setStudentId(rs.getString("student_id"));
                acc.setStudentName(rs.getString("full_name")); // Lấy tên sinh viên từ cột full_name
                acc.setStatus(rs.getInt("status"));
                acc.setActivationDate(rs.getDate("activation_date"));
                
                accountList.add(acc); // Nhét vào danh sách
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }

    // Hàm nhận danh sách tài khoản và lưu tất cả vào Database
    public void insertAccountList(List<EmailAccount> accounts) {
        // Cấu trúc SQL (Tùy thuộc vào tên cột trong bảng email_accounts của bạn)
        String sql = "INSERT INTO email_accounts (student_id, email_address, status) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Tắt tự động lưu để tăng tốc (Gom cục)
            conn.setAutoCommit(false); 

            for (EmailAccount acc : accounts) {
                ps.setString(1, acc.getStudentId());
                ps.setString(2, acc.getEmailAddress());
                ps.setInt(3, acc.getStatus()); // 0: Chờ kích hoạt
                
                ps.addBatch(); // Cho vào giỏ chờ
            }

            ps.executeBatch(); // Đẩy toàn bộ giỏ lên MySQL 1 lần
            conn.commit();     // Xác nhận lưu

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public EmailAccount checkLogin(String username, String password) {
        EmailAccount acc = null;
        String sql = "SELECT * FROM email_accounts WHERE email_address = ? AND password_hash = ?";
        
        // Dùng try-with-resources giống hệt các hàm trên của bạn
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    acc = new EmailAccount();
                    acc.setEmailAddress(rs.getString("email_address"));
                    acc.setStudentId(rs.getString("student_id"));
                    acc.setStatus(rs.getInt("status"));
                    acc.setActivationDate(rs.getDate("activation_date"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return acc;
    }

    // Hàm dùng cho FR-01.5: Đổi mật khẩu và kích hoạt tài khoản
    public boolean activateAccount(String email, String newPassword, String phone) {
        String sql = "UPDATE email_accounts SET password_hash = ?, status = 1 WHERE email_address = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newPassword); 
            ps.setString(2, email);
            
            // executeUpdate() trả về số dòng bị ảnh hưởng, > 0 nghĩa là thành công
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<EmailAccount> searchAccounts(String keyword) {
        List<EmailAccount> accountList = new ArrayList<>();
        String sql = "SELECT e.email_address, s.student_id, s.full_name, e.status, e.activation_date " +
                    "FROM students s " +
                    "JOIN email_accounts e ON s.student_id = e.student_id " +
                    "WHERE s.student_id LIKE ? OR s.full_name LIKE ? " +
                    "ORDER BY e.activation_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                
                // Thêm dấu % vào keyword để tìm kiếm gần đúng (ví dụ: "Doanh" sẽ tìm được "Đỗ Đại Doanh")
                String searchPattern = "%" + keyword + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);

                ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    EmailAccount acc = new EmailAccount();
                    // Mapping dữ liệu từ ResultSet vào EmailAccount (tương tự hàm getAllAccounts)
                    acc.setEmailAddress(rs.getString("email_address"));
                    acc.setStudentId(rs.getString("student_id"));
                    acc.setStudentName(rs.getString("full_name"));
                    acc.setStatus(rs.getInt("status"));
                    acc.setActivationDate(rs.getDate("activation_date"));

                    accountList.add(acc);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        return accountList;
    }

    // FR-02.2 & FR-02.3: Thực thi tạm khóa và lưu số quyết định
    public boolean suspendAccount(String studentId, String decisionNumber) {
        // Cập nhật trạng thái thành 2 (Đang bảo lưu) và cập nhật số quyết định
        String sql = "UPDATE email_accounts SET status = 2, decision_number = ? WHERE student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Set các tham số (Dấu ? thứ nhất là decisionNumber, thứ 2 là studentId)
            ps.setString(1, decisionNumber);
            ps.setString(2, studentId);
            
            // executeUpdate() trả về số dòng bị ảnh hưởng. Nếu > 0 nghĩa là update thành công
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}