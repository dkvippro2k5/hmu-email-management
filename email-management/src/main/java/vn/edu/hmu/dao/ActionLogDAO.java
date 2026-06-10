package vn.edu.hmu.dao;

import vn.edu.hmu.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActionLogDAO {
    
    public boolean insertLog(int adminId, String targetEmail, String actionType, String reason) {
        String sql = "INSERT INTO action_log (admin_id, target_email, action_type, reason, action_time) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setString(2, targetEmail);
            ps.setString(3, actionType);
            ps.setString(4, reason);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
