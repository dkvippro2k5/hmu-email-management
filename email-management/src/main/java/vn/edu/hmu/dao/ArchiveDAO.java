package vn.edu.hmu.dao;

import vn.edu.hmu.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArchiveDAO {

    public boolean insertArchiveM01(String originalFilename, String storedFilepath, int uploadedBy) {
        String sql = "INSERT INTO archive_m01 (original_filename, stored_filepath, uploaded_by) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, originalFilename);
            pstmt.setString(2, storedFilepath);
            pstmt.setInt(3, uploadedBy);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertArchiveM02(String actionType, String decisionNumber, String originalFilename, String storedFilepath, int uploadedBy) {
        String sql = "INSERT INTO archive_m02 (action_type, decision_number, original_filename, stored_filepath, uploaded_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, actionType);
            pstmt.setString(2, decisionNumber);
            pstmt.setString(3, originalFilename);
            pstmt.setString(4, storedFilepath);
            pstmt.setInt(5, uploadedBy);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertArchivePL01(String studentEmail, String studentName, String emailSubject, String emailContent) {
        String sql = "INSERT INTO archive_pl01 (student_email, student_name, email_subject, email_content) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentEmail);
            pstmt.setString(2, studentName);
            pstmt.setString(3, emailSubject);
            pstmt.setString(4, emailContent);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
