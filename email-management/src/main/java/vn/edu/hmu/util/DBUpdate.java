package vn.edu.hmu.util;

import java.sql.Connection;
import java.sql.Statement;

public class DBUpdate {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("ALTER TABLE action_logs ADD COLUMN details LONGTEXT;");
            System.out.println("ALTER TABLE executed successfully.");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
