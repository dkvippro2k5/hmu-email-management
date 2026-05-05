package vn.edu.hmu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import vn.edu.hmu.model.ITAdmin;
import vn.edu.hmu.util.DBConnection;

public class AdminDAO {
    public ITAdmin checkLogin(String username, String password) {
        ITAdmin admin = null;
        // Câu lệnh SQL BẮT BUỘC phải như thế này:
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
                admin.setFullName(rs.getString("full_name"));
                admin.setRole(rs.getString("role"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return admin;
    }
}