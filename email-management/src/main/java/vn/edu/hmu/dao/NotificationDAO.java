package vn.edu.hmu.dao;

import vn.edu.hmu.model.Notification;
import vn.edu.hmu.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public NotificationDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS notifications (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "student_id VARCHAR(50) NOT NULL, " +
                     "title VARCHAR(255) NOT NULL, " +
                     "message TEXT NOT NULL, " +
                     "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                     "is_read TINYINT(1) DEFAULT 0, " +
                     "CONSTRAINT fk_notification_student FOREIGN KEY (student_id) REFERENCES email_accounts(student_id) ON DELETE CASCADE" +
                     ")";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertNotification(Notification n) {
        String sql = "INSERT INTO notifications (student_id, title, message, created_at, is_read) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, n.getStudentId());
            ps.setString(2, n.getTitle());
            ps.setString(3, n.getMessage());
            ps.setTimestamp(4, new java.sql.Timestamp(n.getCreatedAt().getTime()));
            ps.setInt(5, n.isRead() ? 1 : 0);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getNotificationsForStudent(String studentId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE student_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setStudentId(rs.getString("student_id"));
                n.setTitle(rs.getString("title"));
                n.setMessage(rs.getString("message"));
                n.setCreatedAt(rs.getTimestamp("created_at"));
                n.setRead(rs.getInt("is_read") == 1);
                list.add(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countUnread(String studentId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE student_id = ? AND is_read = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
