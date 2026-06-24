<%@ page import="vn.edu.hmu.util.DBConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String message = "";
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement()) {
        
        // Add details column to action_logs if not exists
        try {
            stmt.executeUpdate("ALTER TABLE action_logs ADD COLUMN details LONGTEXT");
            message += "<div style='color:green'>[OK] Đã thêm cột details vào action_logs.</div>";
        } catch (Exception e) {
            message += "<div style='color:#666'>[INFO] Cột details có thể đã tồn tại trong action_logs: " + e.getMessage() + "</div>";
        }

        // Create archive_m02 if not exists
        String createM02 = "CREATE TABLE IF NOT EXISTS archive_m02 (" +
                           "    id INT AUTO_INCREMENT PRIMARY KEY," +
                           "    action_type VARCHAR(50) NOT NULL," +
                           "    decision_number VARCHAR(100)," +
                           "    original_filename VARCHAR(255)," +
                           "    stored_filepath VARCHAR(255) NOT NULL," +
                           "    uploaded_by INT," +
                           "    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                           ")";
        stmt.executeUpdate(createM02);
        message += "<div style='color:green'>[OK] Đã kiểm tra/tạo bảng archive_m02.</div>";

        // Create archive_m01 if not exists
        String createM01 = "CREATE TABLE IF NOT EXISTS archive_m01 (" +
                           "    id INT AUTO_INCREMENT PRIMARY KEY," +
                           "    original_filename VARCHAR(255)," +
                           "    stored_filepath VARCHAR(255) NOT NULL," +
                           "    uploaded_by INT," +
                           "    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                           ")";
        stmt.executeUpdate(createM01);
        message += "<div style='color:green'>[OK] Đã kiểm tra/tạo bảng archive_m01.</div>";

        // Create archive_pl01 if not exists
        String createPL01 = "CREATE TABLE IF NOT EXISTS archive_pl01 (" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY," +
                            "    student_email VARCHAR(100)," +
                            "    student_name VARCHAR(100)," +
                            "    email_subject VARCHAR(255)," +
                            "    email_content TEXT," +
                            "    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")";
        stmt.executeUpdate(createPL01);
        message += "<div style='color:green'>[OK] Đã kiểm tra/tạo bảng archive_pl01.</div>";

    } catch (Exception ex) {
        message += "<div style='color:red'>[LỖI] Kết nối CSDL: " + ex.getMessage() + "</div>";
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Update Database Schema</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f4f6f8; color: #333; padding: 40px; }
        .card { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); max-width: 600px; margin: 0 auto; }
        h3 { margin-top: 0; color: #2c3e50; }
        .btn { display: inline-block; margin-top: 20px; padding: 10px 20px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; }
        .btn:hover { background: #2980b9; }
    </style>
</head>
<body>
    <div class="card">
        <h3>Tiến trình Cập nhật Database</h3>
        <hr>
        <%= message %>
        <br>
        <a href="dashboard" class="btn">⬅ Quay lại Dashboard</a>
    </div>
</body>
</html>
