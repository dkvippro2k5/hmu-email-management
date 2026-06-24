<%@ page import="java.sql.*" %>
<%@ page import="vn.edu.hmu.util.DBConnection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    out.println("<h3>Alter Table Email Accounts</h3>");
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement()) {
        stmt.executeUpdate("ALTER TABLE email_accounts ADD COLUMN initial_password_encrypted VARCHAR(255) DEFAULT NULL;");
        out.println("<p>Success!</p>");
    } catch (Exception e) {
        out.println("<p>Error: " + e.getMessage() + "</p>");
    }
%>
