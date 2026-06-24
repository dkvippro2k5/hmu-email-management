package vn.edu.hmu.test;

import vn.edu.hmu.util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class DeleteData {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
                    stmt.execute("TRUNCATE TABLE email_accounts;");
                    stmt.execute("TRUNCATE TABLE students;");
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
                    System.out.println("Data deleted successfully!");
                }
            } else {
                System.out.println("Failed to connect to DB.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
