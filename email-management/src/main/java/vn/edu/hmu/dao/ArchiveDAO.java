package vn.edu.hmu.dao;

import vn.edu.hmu.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArchiveDAO {

    public boolean insertArchiveM01(int stt, String fullName, String emailAddress, String studentId, String cohort, int uploadedBy) {
        String sql = "INSERT INTO archive_m01 (stt, full_name, email_address, student_id, cohort, uploaded_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, stt);
            pstmt.setString(2, fullName);
            pstmt.setString(3, emailAddress);
            pstmt.setString(4, studentId);
            pstmt.setString(5, cohort);
            pstmt.setInt(6, uploadedBy);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertArchiveM02(String actionType, String decisionNumber, int stt, String fullName, String emailAddress, String studentId, String cohort, int uploadedBy) {
        String sql = "INSERT INTO archive_m02 (action_type, decision_number, stt, full_name, email_address, student_id, cohort, uploaded_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, actionType);
            pstmt.setString(2, decisionNumber);
            pstmt.setInt(3, stt);
            pstmt.setString(4, fullName);
            pstmt.setString(5, emailAddress);
            pstmt.setString(6, studentId);
            pstmt.setString(7, cohort);
            pstmt.setInt(8, uploadedBy);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertArchivePL01(String emailAddress, String fullName, String decisionNumber, String notificationContent) {
        String sql = "INSERT INTO archive_pl01 (email_address, full_name, decision_number, notification_content) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, emailAddress);
            pstmt.setString(2, fullName);
            pstmt.setString(3, decisionNumber);
            pstmt.setString(4, notificationContent);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
