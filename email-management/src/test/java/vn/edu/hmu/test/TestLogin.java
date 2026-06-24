package vn.edu.hmu.test;

import vn.edu.hmu.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestLogin {
    public static void main(String[] args) {
        String email = "duyenmt23435918@hmu.edu.vn";
        String plainPassword = "Hmu@896658697074";
        
        System.out.println("TESTING LOGIN FOR: " + email);
        
        String sql = "SELECT a.password_hash, a.status, s.cccd FROM email_accounts a " +
                     "LEFT JOIN students s ON a.student_id = s.student_id " +
                     "WHERE a.email_address = ?";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                int status = rs.getInt("status");
                String cccd = rs.getString("cccd");
                
                System.out.println("Found account in DB!");
                System.out.println("Status: " + status);
                System.out.println("CCCD from students table: " + cccd);
                System.out.println("Stored Hash: " + hash);
                
                boolean match = BCrypt.checkpw(plainPassword, hash);
                System.out.println("Password Match? " + match);
                
                if (!match && cccd != null) {
                    String altPassword = "Hmu@" + cccd;
                    System.out.println("Trying alt password: " + altPassword);
                    boolean matchAlt = BCrypt.checkpw(altPassword, hash);
                    System.out.println("Alt Password Match? " + matchAlt);
                }
            } else {
                System.out.println("Account not found in email_accounts table.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
