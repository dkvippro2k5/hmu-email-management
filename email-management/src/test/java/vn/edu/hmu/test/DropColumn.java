package vn.edu.hmu.test;

import vn.edu.hmu.util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class DropColumn {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE students DROP COLUMN personal_email;");
                    System.out.println("Column dropped successfully!");
                }
            } else {
                System.out.println("Failed to connect to DB.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
