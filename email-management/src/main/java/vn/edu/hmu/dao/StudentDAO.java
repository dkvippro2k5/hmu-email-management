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
        String sqlStudent = "INSERT INTO students (student_id, full_name, cccd, first_name, last_name, cohort, phone_number, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
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
                ps1.setString(3, student.getCccd());
                ps1.setString(4, student.getFirstName());
                ps1.setString(5, student.getLastName());
                ps1.setString(6, student.getCohort());
                ps1.setString(7, student.getPhoneNumber());
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
        String sql = "SELECT e.email_address, e.student_id, s.full_name, s.cccd, s.first_name, s.last_name, s.cohort, s.phone_number, e.status, e.activation_date " +
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
                acc.setCccd(rs.getString("cccd"));
                acc.setFirstName(rs.getString("first_name"));
                acc.setLastName(rs.getString("last_name"));
                acc.setCohort(rs.getString("cohort"));
                acc.setPhoneNumber(rs.getString("phone_number"));
                acc.setStatus(rs.getInt("status"));
                acc.setActivationDate(rs.getDate("activation_date"));
                
                accountList.add(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }

    public List<EmailAccount> searchAccountsAdvanced(String keyword, int status, String cohort) {
        List<EmailAccount> accountList = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT e.email_address, s.student_id, s.full_name, s.cccd, s.first_name, s.last_name, s.cohort, s.phone_number, e.status, e.activation_date, e.scheduled_delete_date " +
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
                acc.setCccd(rs.getString("cccd"));
                acc.setFirstName(rs.getString("first_name"));
                acc.setLastName(rs.getString("last_name"));
                acc.setCohort(rs.getString("cohort"));
                acc.setPhoneNumber(rs.getString("phone_number"));
                acc.setStatus(rs.getInt("status"));
                acc.setActivationDate(rs.getDate("activation_date"));
                try {
                    acc.setScheduledDeleteDate(rs.getDate("scheduled_delete_date"));
                } catch (Exception ignore) {}

                accountList.add(acc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }

    public List<EmailAccount> exportAccountsAdvanced(List<Integer> statuses, String cohort) {
        List<EmailAccount> accountList = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT e.email_address, s.student_id, s.full_name, s.cccd, s.first_name, s.last_name, s.cohort, s.phone_number, e.status, e.activation_date, e.scheduled_delete_date " +
            "FROM students s " +
            "JOIN email_accounts e ON s.student_id = e.student_id " +
            "WHERE 1=1 "
        );
        
        List<Object> params = new ArrayList<>();

        if (statuses != null && !statuses.isEmpty()) {
            sql.append("AND e.status IN (");
            for (int i = 0; i < statuses.size(); i++) {
                sql.append("?");
                if (i < statuses.size() - 1) sql.append(",");
                params.add(statuses.get(i));
            }
            sql.append(") ");
        }

        if (cohort != null && !cohort.isEmpty()) {
            sql.append("AND s.cohort LIKE ? ");
            params.add("%" + cohort + "%");
        }
        
        sql.append("ORDER BY s.student_id ASC");
        
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
                acc.setStatus(rs.getInt("status"));
                if (rs.getTimestamp("activation_date") != null) {
                    acc.setActivationDate(new java.sql.Date(rs.getTimestamp("activation_date").getTime()));
                }
                if (rs.getTimestamp("scheduled_delete_date") != null) {
                    acc.setScheduledDeleteDate(new java.sql.Date(rs.getTimestamp("scheduled_delete_date").getTime()));
                }
                
                acc.setStudentName(rs.getString("full_name"));
                acc.setCccd(rs.getString("cccd"));
                acc.setFirstName(rs.getString("first_name"));
                acc.setLastName(rs.getString("last_name"));
                acc.setCohort(rs.getString("cohort"));
                acc.setPhoneNumber(rs.getString("phone_number"));
                
                accountList.add(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return accountList;
    }

    public boolean updateStudentInfo(String studentId, Student student, String emailAddress, int status) {
        String sqlStudent = "UPDATE students SET full_name = ?, cccd = ?, first_name = ?, last_name = ?, cohort = ?, phone_number = ? WHERE student_id = ?";
        String sqlEmail = "UPDATE email_accounts SET email_address = ?, status = ? WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlStudent);
                 PreparedStatement ps2 = conn.prepareStatement(sqlEmail)) {

                ps1.setString(1, student.getFullName());
                ps1.setString(2, student.getCccd());
                ps1.setString(3, student.getFirstName());
                ps1.setString(4, student.getLastName());
                ps1.setString(5, student.getCohort());
                ps1.setString(6, student.getPhoneNumber());
                ps1.setString(7, studentId);
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
        String sqlStudent = "INSERT INTO students (student_id, full_name, cccd, first_name, last_name, cohort, phone_number, portal_password, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        String sqlEmail = "INSERT INTO email_accounts (email_address, student_id, password_hash, initial_password_encrypted, status, activation_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlStudent);
                 PreparedStatement ps2 = conn.prepareStatement(sqlEmail)) {

                ps1.setString(1, student.getStudentId());
                ps1.setString(2, student.getFullName());
                ps1.setString(3, student.getCccd());
                ps1.setString(4, student.getFirstName());
                ps1.setString(5, student.getLastName());
                ps1.setString(6, student.getCohort());
                ps1.setString(7, student.getPhoneNumber());
                ps1.setString(8, student.getPortalPassword());
                ps1.executeUpdate();

                ps2.setString(1, emailAcc.getEmailAddress());
                ps2.setString(2, emailAcc.getStudentId());
                ps2.setString(3, emailAcc.getPasswordHash());
                ps2.setString(4, emailAcc.getInitialPasswordEncrypted());
                ps2.setInt(5, emailAcc.getStatus());
                if (emailAcc.getActivationDate() != null) {
                    ps2.setTimestamp(6, new java.sql.Timestamp(emailAcc.getActivationDate().getTime()));
                } else {
                    ps2.setTimestamp(6, null);
                }
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

    public boolean createStudentsWithEmailsBatch(java.util.List<Student> students, java.util.List<EmailAccount> emailAccs) {
        if (students.size() != emailAccs.size()) return false;

        String sqlStudent = "INSERT INTO students (student_id, full_name, cccd, first_name, last_name, cohort, phone_number, portal_password, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        String sqlEmail = "INSERT INTO email_accounts (email_address, student_id, password_hash, initial_password_encrypted, status, activation_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlStudent);
                 PreparedStatement ps2 = conn.prepareStatement(sqlEmail)) {

                for (int i = 0; i < students.size(); i++) {
                    Student student = students.get(i);
                    EmailAccount emailAcc = emailAccs.get(i);

                    ps1.setString(1, student.getStudentId());
                    ps1.setString(2, student.getFullName());
                    ps1.setString(3, student.getCccd());
                    ps1.setString(4, student.getFirstName());
                    ps1.setString(5, student.getLastName());
                    ps1.setString(6, student.getCohort());
                    ps1.setString(7, student.getPhoneNumber());
                    ps1.setString(8, student.getPortalPassword());
                    ps1.addBatch();

                    ps2.setString(1, emailAcc.getEmailAddress());
                    ps2.setString(2, emailAcc.getStudentId());
                    ps2.setString(3, emailAcc.getPasswordHash());
                    ps2.setString(4, emailAcc.getInitialPasswordEncrypted());
                    ps2.setInt(5, emailAcc.getStatus());
                    if (emailAcc.getActivationDate() != null) {
                        ps2.setTimestamp(6, new java.sql.Timestamp(emailAcc.getActivationDate().getTime()));
                    } else {
                        ps2.setTimestamp(6, null);
                    }
                    ps2.addBatch();
                }

                ps1.executeBatch();
                ps2.executeBatch();

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
        String sql = "UPDATE email_accounts SET status = 2, decision_number = ?, activation_date = CURRENT_DATE() WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, decisionNumber);
            ps.setString(2, studentId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restoreAccount(String studentId) {
        String sql = "UPDATE email_accounts SET status = 1, decision_number = NULL, scheduled_delete_date = NULL WHERE student_id = ? AND status IN (2, 3)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markPendingDelete(String studentId, String decisionNumber) {
        String sql = "UPDATE email_accounts SET status = 3, scheduled_delete_date = DATE_ADD(NOW(), INTERVAL 30 DAY), decision_number = ?, activation_date = CURRENT_DATE() WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, decisionNumber);
            ps.setString(2, studentId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean permanentlyDeleteAccount(String studentId) {
        String sqlEmail = "DELETE FROM email_accounts WHERE student_id = ?";
        String sqlStudent = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlEmail);
                 PreparedStatement ps2 = conn.prepareStatement(sqlStudent)) {
                ps1.setString(1, studentId);
                ps1.executeUpdate();
                
                ps2.setString(1, studentId);
                ps2.executeUpdate();
                
                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            }
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
                    s.setCccd(rs.getString("cccd"));
                    s.setFirstName(rs.getString("first_name"));
                    s.setLastName(rs.getString("last_name"));
                    s.setCohort(rs.getString("cohort"));
                    s.setPhoneNumber(rs.getString("phone_number"));

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

    public EmailAccount checkLogin(String username, String plainPassword) {
        String sql = "SELECT e.*, s.cccd, s.portal_password FROM email_accounts e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "WHERE e.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int status = rs.getInt("status");
                    String dbCccd = rs.getString("cccd");
                    String portalHash = rs.getString("portal_password");

                    boolean loginSuccess = false;

                    if (portalHash != null && !portalHash.isEmpty()) {
                        try {
                            if (org.mindrot.jbcrypt.BCrypt.checkpw(plainPassword, portalHash)) {
                                loginSuccess = true;
                            }
                        } catch (Exception e) {
                            if (plainPassword.equals(portalHash)) loginSuccess = true;
                        }
                    } else {
                        if (plainPassword.equals(dbCccd)) {
                            loginSuccess = true;
                        }
                    }

                    if (loginSuccess) {
                        EmailAccount acc = new EmailAccount();
                        acc.setEmailAddress(rs.getString("email_address"));
                        acc.setStudentId(rs.getString("student_id"));
                        acc.setPasswordHash(rs.getString("password_hash"));
                        acc.setStatus(status);
                        acc.setInitialPasswordEncrypted(rs.getString("initial_password_encrypted"));
                        return acc;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean isPortalPasswordNull(String studentId) {
        String sql = "SELECT portal_password, cccd FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String pp = rs.getString("portal_password");
                    String cccd = rs.getString("cccd");
                    
                    if (pp == null || pp.trim().isEmpty()) {
                        return true;
                    }
                    
                    // Nếu password đang là mã băm của CCCD (mặc định khi import), coi như chưa đổi
                    try {
                        if (cccd != null && !cccd.isEmpty() && org.mindrot.jbcrypt.BCrypt.checkpw(cccd, pp)) {
                            return true;
                        }
                    } catch (Exception ignore) {}
                    
                    return false;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return true;
    }

    public boolean updatePortalPassword(String studentId, String hashedNewPassword) {
        String sql = "UPDATE students SET portal_password = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedNewPassword);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudentPhone(String studentId, String phone) {
        String sql = "UPDATE students SET phone_number = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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
        // Tạm thời vô hiệu hóa kiểm tra 24h để phục vụ Test (Cách 1)
        // String sql = "SELECT e.* FROM email_accounts e JOIN students s ON e.student_id = s.student_id WHERE e.status = 0 AND s.created_at < DATE_SUB(NOW(), INTERVAL 1 DAY)";
        String sql = "SELECT e.* FROM email_accounts e JOIN students s ON e.student_id = s.student_id WHERE e.status = 0";
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

    public List<EmailAccount> getAccountsPendingAutoDelete() {
        List<EmailAccount> accounts = new ArrayList<>();
        String sql = "SELECT * FROM email_accounts WHERE status = 3 AND scheduled_delete_date <= CURRENT_DATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EmailAccount acc = new EmailAccount();
                acc.setEmailAddress(rs.getString("email_address"));
                acc.setStudentId(rs.getString("student_id"));
                acc.setStatus(rs.getInt("status"));
                accounts.add(acc);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return accounts;
    }

    public EmailAccount getAccountByEmail(String email) {
        String sql = "SELECT e.*, s.full_name, s.phone_number FROM email_accounts e JOIN students s ON e.student_id = s.student_id WHERE e.email_address = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EmailAccount acc = new EmailAccount();
                    acc.setEmailAddress(rs.getString("email_address"));
                    acc.setStudentId(rs.getString("student_id"));
                    acc.setStudentName(rs.getString("full_name"));
                    acc.setPhoneNumber(rs.getString("phone_number"));
                    acc.setStatus(rs.getInt("status"));
                    return acc;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public EmailAccount getAccountByStudentId(String studentId) {
        String sql = "SELECT e.*, s.full_name, s.phone_number FROM email_accounts e JOIN students s ON e.student_id = s.student_id WHERE e.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EmailAccount acc = new EmailAccount();
                    acc.setEmailAddress(rs.getString("email_address"));
                    acc.setStudentId(rs.getString("student_id"));
                    acc.setStudentName(rs.getString("full_name"));
                    acc.setPhoneNumber(rs.getString("phone_number"));

                    acc.setStatus(rs.getInt("status"));
                    acc.setInitialPasswordEncrypted(rs.getString("initial_password_encrypted"));
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

    public List<EmailAccount> getSuspendedAccountsList() {
        List<EmailAccount> accountList = new ArrayList<>();
        String sql = "SELECT e.email_address, e.student_id, s.full_name, s.cccd, s.first_name, s.last_name, s.cohort, s.phone_number, e.status, e.activation_date " +
                     "FROM email_accounts e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "WHERE e.status = 2 " +
                     "ORDER BY e.activation_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EmailAccount acc = new EmailAccount();
                acc.setEmailAddress(rs.getString("email_address"));
                acc.setStudentId(rs.getString("student_id"));
                acc.setStudentName(rs.getString("full_name"));
                acc.setCccd(rs.getString("cccd"));
                acc.setFirstName(rs.getString("first_name"));
                acc.setLastName(rs.getString("last_name"));
                acc.setCohort(rs.getString("cohort"));
                acc.setPhoneNumber(rs.getString("phone_number"));
                acc.setStatus(rs.getInt("status"));
                acc.setActivationDate(rs.getDate("activation_date"));
                
                accountList.add(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }
    public List<EmailAccount> getPendingRevokeAccountsList() {
        List<EmailAccount> accountList = new ArrayList<>();
        String sql = "SELECT e.email_address, e.student_id, s.full_name, s.cccd, s.first_name, s.last_name, s.cohort, s.phone_number, e.status, e.activation_date, e.scheduled_delete_date " +
                     "FROM email_accounts e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "WHERE e.status = 3 " +
                     "ORDER BY e.scheduled_delete_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EmailAccount acc = new EmailAccount();
                acc.setEmailAddress(rs.getString("email_address"));
                acc.setStudentId(rs.getString("student_id"));
                acc.setStudentName(rs.getString("full_name"));
                acc.setCccd(rs.getString("cccd"));
                acc.setFirstName(rs.getString("first_name"));
                acc.setLastName(rs.getString("last_name"));
                acc.setCohort(rs.getString("cohort"));
                acc.setPhoneNumber(rs.getString("phone_number"));
                acc.setStatus(rs.getInt("status"));
                acc.setActivationDate(rs.getDate("activation_date"));
                
                try {
                    acc.setScheduledDeleteDate(rs.getDate("scheduled_delete_date"));
                } catch (Exception ignore) {}
                
                accountList.add(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }
}
