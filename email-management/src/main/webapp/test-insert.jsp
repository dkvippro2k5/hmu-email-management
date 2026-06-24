<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="vn.edu.hmu.dao.AdminDAO, vn.edu.hmu.model.ActionLog, vn.edu.hmu.model.ITAdmin" %>
<%@ page import="java.sql.*, vn.edu.hmu.util.DBConnection" %>
<html>
<body>
<%
    out.println("<h3>Testing Database...</h3>");
    try {
        ITAdmin admin = (ITAdmin) request.getSession().getAttribute("currentAdmin");
        out.println("Session admin: " + (admin != null ? admin.getUsername() : "null") + "<br>");
        
        int testAdminId = 1;
        if (admin != null && admin.getAdminID() != null) {
            testAdminId = Integer.parseInt(admin.getAdminID());
            out.println("Admin ID from session: " + testAdminId + "<br>");
        } else {
            out.println("Admin ID from session is NULL or admin is NULL. Using 1.<br>");
        }
        
        // Try direct SQL
        String sql = "INSERT INTO action_logs (admin_id, target_email, action_type, reason, details, action_time) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, testAdminId);
            ps.setString(2, "TEST");
            ps.setString(3, "TEST_TYPE");
            ps.setString(4, "TEST");
            ps.setString(5, "TEST");
            int rows = ps.executeUpdate();
            out.println("<b>Direct SQL Execute Update returned: " + rows + "</b><br>");
        } catch (SQLException e) {
            out.println("<b style='color:red'>SQL EXCEPTION: " + e.getMessage() + "</b><br>");
            out.println("SQLState: " + e.getSQLState() + "<br>");
            out.println("ErrorCode: " + e.getErrorCode() + "<br>");
        }
    } catch (Exception e) {
        out.println("<b>General Exception:</b> " + e.getMessage());
    }
%>
</body>
</html>
