<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*, vn.edu.hmu.util.DBConnection" %>
<html>
<head><title>Test Logs</title></head>
<body>
    <h2>Raw Action Logs from DB</h2>
    <table border="1">
        <tr><th>ID</th><th>Admin ID</th><th>Email</th><th>Action Type</th><th>Reason</th><th>Details</th><th>Time</th></tr>
        <%
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM action_logs ORDER BY action_time DESC LIMIT 20")) {
                while (rs.next()) {
        %>
        <tr>
            <td><%= rs.getInt("log_id") %></td>
            <td><%= rs.getInt("admin_id") %></td>
            <td><%= rs.getString("target_email") %></td>
            <td><%= rs.getString("action_type") %></td>
            <td><%= rs.getString("reason") %></td>
            <td><%= rs.getString("details") %></td>
            <td><%= rs.getTimestamp("action_time") %></td>
        </tr>
        <%
                }
            } catch (Exception e) {
                out.println("Error: " + e.getMessage());
            }
        %>
    </table>
</body>
</html>
