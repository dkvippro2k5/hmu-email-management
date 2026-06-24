package vn.edu.hmu.dao;

import vn.edu.hmu.model.SupportRequest;
import vn.edu.hmu.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SupportRequestDAO {

    public boolean insertRequest(SupportRequest request) {
        String sql = "INSERT INTO support_requests (student_id, subject, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, request.getStudentId());
            ps.setString(2, request.getSubject());
            ps.setString(3, request.getContent());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SupportRequest> getAllRequests() {
        List<SupportRequest> list = new ArrayList<>();
        String sql = "SELECT r.*, s.full_name FROM support_requests r LEFT JOIN students s ON r.student_id = s.student_id ORDER BY r.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SupportRequest r = new SupportRequest();
                r.setRequestId(rs.getInt("request_id"));
                r.setStudentId(rs.getString("student_id"));
                r.setSubject(rs.getString("subject"));
                r.setContent(rs.getString("content"));
                r.setStatus(rs.getInt("status"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                r.setStudentName(rs.getString("full_name"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getUnreadCount() {
        String sql = "SELECT COUNT(*) FROM support_requests WHERE status = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateStatus(int requestId, int status) {
        String sql = "UPDATE support_requests SET status = ? WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
