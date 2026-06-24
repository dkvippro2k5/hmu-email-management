<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="vn.edu.hmu.dao.StudentDAO" %>
<%@ page import="vn.edu.hmu.model.EmailAccount" %>
<%@ page import="java.util.List" %>
<html>
<body>
<h3>Checking Auto Activation</h3>
<%
    StudentDAO dao = new StudentDAO();
    List<EmailAccount> pending = dao.getAccountsPendingAutoActivation();
    out.println("Found " + pending.size() + " accounts pending.<br/>");
    for (EmailAccount acc : pending) {
        out.println("Email: " + acc.getEmailAddress() + " - Status: " + acc.getStatus() + "<br/>");
    }
%>
</body>
</html>
