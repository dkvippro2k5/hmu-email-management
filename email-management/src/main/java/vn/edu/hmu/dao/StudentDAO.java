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

    public String importStudentAndEmail(Student student, EmailAccount emailAcc) {
        String sqlStudent = "INSERT INTO students (student_id, full_name, gender, date_of_birth, class_name, department, major, cohort, personal_email, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        String sqlEmail = "INSERT INTO email_accounts (email_address, student_id, password_hash, status, activation_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return "Không thể kết nối đến database.";
            }
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlStudent);
                 PreparedStatement ps2 = conn.prepareStatement(sqlEmail)) {

                ps1.setString(1, student.getStudentId());
                ps1.setString(2, student.getFullName());
                ps1.setString(3, student.getGender());
                ps1.setString(4, student.getDateOfBirth());
                ps1.setString(5, student.getClassName());
                ps1.setString(6, student.getDepartment());
                ps1.setString(7, student.getMajor());
                ps1.setString(8, student.getCohort());
                ps1.setString(9, student.getPersonalEmail());
                ps1.executeUpdate();

                ps2.setString(1, emailAcc.getEmailAddress());
                ps2.setString(2, emailAcc.getStudentId());
                ps2.setString(3, emailAcc.getPasswordHash());
                ps2.setInt(4, emailAcc.getStatus());
                ps2.setDate(5, emailAcc.getActivationDate());
                ps2.executeUpdate();

                conn.commit();
                return null;
            } catch (Exception e) {
                conn.rollback();
                return e.getMessage();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public List<EmailAccount> getAllAccounts() {
        List<EmailAccount> accountList = new ArrayList<>();
        String sql = "SELECT e.email_address, e.student_id, s.full_name, s.gender, s.date_of_birth, s.class_name, s.department, s.major, s.cohort, s.personal_email, e.status, e.activation_date " +
                     "FROM email_accounts e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "ORDER BY e.activation_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EmailAccount acc = new EmailAccount();
                acc.setEmailAddress(rs.getString("email_address"));
                acc.setStudentId(rs.getString("student_id"));
                acc.setStudentName(rs.getString("full_name"));
                acc.setGender(rs.getString("gender"));
                acc.setDateOfBirth(rs.getString("date_of_birth"));
                acc.setClassName(rs.getString("class_name"));
                acc.setDepartment(rs.getString("department"));
                acc.setMajor(rs.getString("major"));
                acc.setCohort(rs.getString("cohort"));
                acc.setPersonalEmail(rs.getString("personal_email"));
                acc.setStatus(rs.getInt("status"));
                acc.setActivationDate(rs.getDate("activation_date"));
                
                accountList.add(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }

    public List<EmailAccount> searchAccountsAdvanced(String keyword, int status, String className, String department, String major, String cohort) {
        List<EmailAccount> accountList = new ArrayList<>();
        
        // Sửa lỗi: e.email_address thay vì s.email_address
        StringBuilder sql = new StringBuilder(
            "SELECT e.email_address, s.student_id, s.full_name, s.gender, s.date_of_birth, s.class_name, s.department, s.major, s.cohort, s.personal_email, e.status, e.activation_date " +
            "FROM students s " +
            "JOIN email_accounts e ON s.student_id = e.student_id " +
            "WHERE (s.student_id LIKE ? OR s.full_name LIKE ? OR e.email_address LIKE ?) "
        );
        
        List<Object> params = new ArrayList<>();
        String searchPattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
        params.add(searchPattern);
        params.add(searchPattern);
        params.add(searchPattern);

        if (status != -1) {
            sql.append("AND e.status = ? ");
            params.add(status);
        }
        if (className != null && !className.isEmpty()) {
            sql.append("AND s.class_name LIKE ? ");
            params.add("%" + className + "%");
        }
        if (department != null && !department.isEmpty()) {
            sql.append("AND s.department LIKE ? ");
            params.add("%" + department + "%");
        }
        if (major != null && !major.isEmpty()) {
            sql.append("AND s.major LIKE ? ");
            params.add("%" + major + "%");
        }
        if (cohort != null && !cohort.isEmpty()) {
            sql.append("AND s.cohort LIKE ? ");
            params.add("%" + cohort + "%");
        }
        
        sql.append("ORDER BY e.activation_date DESC");
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                EmailAccount acc = new EmailAccount();
                acc.setEmailAddress(rs.getString("email_address"));
                acc.setStudentId(rs.getString("student_id"));
                acc.setStudentName(rs.getString("full_name"));
                acc.setGender(rs.getString("gender"));
                acc.setDateOfBirth(rs.getString("date_of_birth"));
                acc.setClassName(rs.getString("class_name"));
                acc.setDepartment(rs.getString("department"));
                acc.setMajor(rs.getString("major"));
                acc.setCohort(rs.getString("cohort"));
                acc.setPersonalEmail(rs.getString("personal_email"));
                acc.setStatus(rs.getInt("status"));
                acc.setActivationDate(rs.getDate("activation_date"));

                accountList.add(acc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }

    public boolean updateStudentInfo(String studentId, Student student, String emailAddress, int status) {
        String sqlStudent = "UPDATE students SET full_name = ?, gender = ?, date_of_birth = ?, class_name = ?, department = ?, major = ?, cohort = ?, personal_email = ? WHERE student_id = ?";
        String sqlEmail = "UPDATE email_accounts SET email_address = ?, status = ? WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlStudent);
                 PreparedStatement ps2 = conn.prepareStatement(sqlEmail)) {

                ps1.setString(1, student.getFullName());
                ps1.setString(2, student.getGender());
                ps1.setString(3, student.getDateOfBirth());
                ps1.setString(4, student.getClassName());
                ps1.setString(5, student.getDepartment());
                ps1.setString(6, student.getMajor());
                ps1.setString(7, student.getCohort());
                ps1.setString(8, student.getPersonalEmail());
                ps1.setString(9, studentId);
                ps1.executeUpdate();

                ps2.setString(1, emailAddress);
                ps2.setInt(2, status);
                ps2.setString(3, studentId);
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createStudentWithEmail(Student student, EmailAccount emailAcc) {
        String sqlStudent = "INSERT INTO students (student_id, full_name, gender, date_of_birth, class_name, department, major, cohort, personal_email, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        String sqlEmail = "INSERT INTO email_accounts (email_address, student_id, password_hash, status, activation_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlStudent);
                 PreparedStatement ps2 = conn.prepareStatement(sqlEmail)) {

                ps1.setString(1, student.getStudentId());
                ps1.setString(2, student.getFullName());
                ps1.setString(3, student.getGender());
                ps1.setString(4, student.getDateOfBirth());
                ps1.setString(5, student.getClassName());
                ps1.setString(6, student.getDepartment());
                ps1.setString(7, student.getMajor());
                ps1.setString(8, student.getCohort());
                ps1.setString(9, student.getPersonalEmail());
                ps1.executeUpdate();

                ps2.setString(1, emailAcc.getEmailAddress());
                ps2.setString(2, emailAcc.getStudentId());
                ps2.setString(3, emailAcc.getPasswordHash());
                ps2.setInt(4, emailAcc.getStatus());
                ps2.setDate(5, emailAcc.getActivationDate());
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean suspendAccount(String studentId, String decisionNumber) {
        String sql = "UPDATE email_accounts SET status = 2, decision_number = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, decisionNumber);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restoreAccount(String studentId) {
        String sql = "UPDATE email_accounts SET status = 1, decision_number = NULL WHERE student_id = ? AND status IN (2, 3)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markPendingDelete(String studentId) {
        String sql = "UPDATE email_accounts SET status = 3 WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String deleteAllStudentsAndEmails() {
        String sqlDisableFk = "SET FOREIGN_KEY_CHECKS = 0";
        String sqlEmail = "DELETE FROM email_accounts";
        String sqlStudent = "DELETE FROM students";
        String sqlEnableFk = "SET FOREIGN_KEY_CHECKS = 1";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return "Không thể kết nối đến database.";
            }
            conn.setAutoCommit(false);
            try (PreparedStatement psDisable = conn.prepareStatement(sqlDisableFk);
                 PreparedStatement ps1 = conn.prepareStatement(sqlEmail);
                 PreparedStatement ps2 = conn.prepareStatement(sqlStudent);
                 PreparedStatement psEnable = conn.prepareStatement(sqlEnableFk)) {
                psDisable.executeUpdate();
                ps1.executeUpdate();
                ps2.executeUpdate();
                psEnable.executeUpdate();
                conn.commit();
                return null;
            } catch (Exception e) {
                conn.rollback();
                return e.getMessage();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Student getStudentById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student();
                    s.setStudentId(rs.getString("student_id"));
                    s.setFullName(rs.getString("full_name"));
                    s.setGender(rs.getString("gender"));
                    s.setDateOfBirth(rs.getString("date_of_birth"));
                    s.setClassName(rs.getString("class_name"));
                    s.setDepartment(rs.getString("department"));
                    s.setMajor(rs.getString("major"));
                    s.setCohort(rs.getString("cohort"));
                    s.setPersonalEmail(rs.getString("personal_email"));
                    return s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalAccounts() {
        String sql = "SELECT COUNT(*) FROM email_accounts";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getActiveAccounts() {
        String sql = "SELECT COUNT(*) FROM email_accounts WHERE status = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getSuspendedAccounts() {
        String sql = "SELECT COUNT(*) FROM email_accounts WHERE status = 2";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getPendingRevokeAccounts() {
        String sql = "SELECT COUNT(*) FROM email_accounts WHERE status = 3";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public EmailAccount checkLogin(String username, String password) {
        String sql = "SELECT * FROM email_accounts WHERE email_address = ? AND password_hash = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EmailAccount acc = new EmailAccount();
                    acc.setEmailAddress(rs.getString("email_address"));
                    acc.setStudentId(rs.getString("student_id"));
                    acc.setStatus(rs.getInt("status"));
                    return acc;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // RESTORED METHODS
    public boolean activateAccount(String email, String newPassword, String phone) {
        String sql = "UPDATE email_accounts SET password_hash = ?, status = 1, activation_date = NOW() WHERE email_address = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword); 
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<EmailAccount> getAccountsPendingAutoActivation() {
        List<EmailAccount> accounts = new ArrayList<>();
        String sql = "SELECT e.* FROM email_accounts e JOIN students s ON e.student_id = s.student_id WHERE e.status = 0 AND s.created_at < DATE_SUB(NOW(), INTERVAL 1 DAY)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EmailAccount acc = new EmailAccount();
                acc.setEmailAddress(rs.getString("email_address"));
                acc.setStudentId(rs.getString("student_id"));
                acc.setStatus(rs.getInt("status"));
                acc.setActivationDate(rs.getDate("activation_date"));
                accounts.add(acc);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return accounts;
    }

    public boolean autoActivateAccount(String emailAddress) {
        String sql = "UPDATE email_accounts SET status = 1, activation_date = NOW() WHERE email_address = ? AND status = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emailAddress);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public EmailAccount getAccountByEmail(String email) {
        String sql = "SELECT e.*, s.full_name, s.personal_email FROM email_accounts e JOIN students s ON e.student_id = s.student_id WHERE e.email_address = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EmailAccount acc = new EmailAccount();
                    acc.setEmailAddress(rs.getString("email_address"));
                    acc.setStudentId(rs.getString("student_id"));
                    acc.setStudentName(rs.getString("full_name"));
                    acc.setPersonalEmail(rs.getString("personal_email"));
                    acc.setStatus(rs.getInt("status"));
                    return acc;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE email_accounts SET password_hash = ? WHERE email_address = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
