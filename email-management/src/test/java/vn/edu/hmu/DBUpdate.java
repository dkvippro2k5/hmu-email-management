package vn.edu.hmu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DBUpdate {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/email_management_db?useUnicode=true&characterEncoding=UTF-8", "root", "28032005");
            Statement stmt = conn.createStatement();
            
            String sql = new String(Files.readAllBytes(Paths.get("C:\\Users\\ZENBOOK\\.gemini\\antigravity\\brain\\2e225fa0-b03f-47f3-9397-7e5a9f77da0f\\scratch\\update_archives.sql")));
            String[] queries = sql.split(";");
            
            for (String query : queries) {
                if (query.trim().length() > 0) {
                    stmt.execute(query);
                    System.out.println("Executed: " + query.substring(0, Math.min(query.length(), 50)).replace("\n", " ") + "...");
                }
            }
            conn.close();
            System.out.println("DB Update Successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
