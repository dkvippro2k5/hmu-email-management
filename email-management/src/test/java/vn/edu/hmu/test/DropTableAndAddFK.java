package vn.edu.hmu.test;

import vn.edu.hmu.util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class DropTableAndAddFK {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Drop support_requests table
            stmt.executeUpdate("DROP TABLE IF EXISTS support_requests");
            System.out.println("Dropped table support_requests.");
            
            // Clean up orphaned records
            stmt.executeUpdate("DELETE FROM archive_m01 WHERE student_id NOT IN (SELECT student_id FROM students)");
            System.out.println("Cleaned orphaned records in archive_m01.");

            // Add foreign keys to archive tables connecting to students by student_id
            try {
                stmt.executeUpdate("ALTER TABLE archive_m01 ADD CONSTRAINT fk_archive_m01_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE ON UPDATE CASCADE");
                System.out.println("Added FK to archive_m01.");
            } catch (Exception e) {
                System.out.println("Failed to add FK to archive_m01: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("ALTER TABLE archive_m02 ADD CONSTRAINT fk_archive_m02_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE ON UPDATE CASCADE");
                System.out.println("Added FK to archive_m02.");
            } catch (Exception e) {
                System.out.println("Failed to add FK to archive_m02: " + e.getMessage());
            }

            // Add student_id to archive_pl01 if not exists
            try {
                stmt.executeUpdate("ALTER TABLE archive_pl01 ADD COLUMN student_id VARCHAR(50) AFTER full_name");
                System.out.println("Added student_id to archive_pl01.");
            } catch (Exception e) {
                System.out.println("student_id might already exist in archive_pl01.");
            }

            // Clean up orphaned records in archive_pl01 before adding FK (if any)
            stmt.executeUpdate("DELETE FROM archive_pl01 WHERE student_id IS NOT NULL AND student_id NOT IN (SELECT student_id FROM students)");
            
            try {
                stmt.executeUpdate("ALTER TABLE archive_pl01 ADD CONSTRAINT fk_archive_pl01_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE ON UPDATE CASCADE");
                System.out.println("Added FK to archive_pl01.");
            } catch (Exception e) {
                System.out.println("Failed to add FK to archive_pl01: " + e.getMessage());
            }

            // Set orphaned uploaded_by to NULL
            stmt.executeUpdate("UPDATE archive_m01 SET uploaded_by = NULL WHERE uploaded_by IS NOT NULL AND uploaded_by NOT IN (SELECT admin_id FROM it_admins)");
            stmt.executeUpdate("UPDATE archive_m02 SET uploaded_by = NULL WHERE uploaded_by IS NOT NULL AND uploaded_by NOT IN (SELECT admin_id FROM it_admins)");
            
            // Add foreign keys for uploaded_by -> it_admins(admin_id)
            try {
                stmt.executeUpdate("ALTER TABLE archive_m01 ADD CONSTRAINT fk_archive_m01_admin FOREIGN KEY (uploaded_by) REFERENCES it_admins(admin_id) ON DELETE SET NULL ON UPDATE CASCADE");
                System.out.println("Added FK uploaded_by to archive_m01.");
            } catch (Exception e) {
                System.out.println("Failed to add FK uploaded_by to archive_m01: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("ALTER TABLE archive_m02 ADD CONSTRAINT fk_archive_m02_admin FOREIGN KEY (uploaded_by) REFERENCES it_admins(admin_id) ON DELETE SET NULL ON UPDATE CASCADE");
                System.out.println("Added FK uploaded_by to archive_m02.");
            } catch (Exception e) {
                System.out.println("Failed to add FK uploaded_by to archive_m02: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
