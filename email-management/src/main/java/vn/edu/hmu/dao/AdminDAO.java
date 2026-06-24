package vn.edu.hmu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import vn.edu.hmu.model.ITAdmin;
import vn.edu.hmu.model.ActionLog;
import vn.edu.hmu.util.DBConnection;
import java.util.List;
import java.util.ArrayList;

public class AdminDAO {
    public ITAdmin checkLogin(String username, String password) {
        ITAdmin admin = null;
        String sql = "SELECT * FROM it_admins WHERE username = ? AND password_hash = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                admin = new ITAdmin();
                admin.setAdminID(rs.getString("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setFullName(rs.getString("full_name"));
                admin.setRole(rs.getString("role"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return admin;
    }

    public List<ActionLog> getRecentLogs(int limit) {
        List<ActionLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM action_logs ORDER BY action_time DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ActionLog log = new ActionLog();
                log.setLogId(rs.getInt("log_id"));
                log.setAdminId(rs.getInt("admin_id"));
                log.setTargetEmail(rs.getString("target_email"));
                log.setActionType(rs.getString("action_type"));
                log.setReason(rs.getString("reason"));
                log.setDetails(rs.getString("details"));
                log.setActionTime(rs.getTimestamp("action_time"));
                logs.add(log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    public boolean insertActionLog(ActionLog log) {
        String sql = "INSERT INTO action_logs (admin_id, target_email, action_type, reason, details, action_time) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, log.getAdminId());
            ps.setString(2, log.getTargetEmail());
            ps.setString(3, log.getActionType());
            ps.setString(4, log.getReason());
            ps.setString(5, log.getDetails());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ActionLog> getLogsByEmail(String email, int limit) {
        List<ActionLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM action_logs WHERE target_email = ? ORDER BY action_time DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ActionLog log = new ActionLog();
                log.setLogId(rs.getInt("log_id"));
                log.setAdminId(rs.getInt("admin_id"));
                log.setTargetEmail(rs.getString("target_email"));
                log.setActionType(rs.getString("action_type"));
                log.setReason(rs.getString("reason"));
                log.setDetails(rs.getString("details"));
                log.setActionTime(rs.getTimestamp("action_time"));
                logs.add(log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }
}
