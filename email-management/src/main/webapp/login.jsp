<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Đăng nhập hệ thống quản lý Email HMU</title>
    <link rel="stylesheet" href="css/login.css">
</head>
<body>
    <div class="login-box">
        <h2>HMU IT Admin</h2>
        <form action="login" method="post">
            <input type="text" name="username" placeholder="Tên đăng nhập (AdminID)" required>
            <input type="password" name="password" placeholder="Mật khẩu" required>
            <div style="margin-bottom: 20px; text-align: center;">
                <label style="margin-right: 15px; cursor: pointer; font-weight: bold;">
                    <input type="radio" name="role" value="student" checked> Sinh viên
                </label>
                <label style="cursor: pointer; font-weight: bold; color: #0f4c75;">
                    <input type="radio" name="role" value="admin">  Cán bộ IT
                </label>
            </div>
            <button type="submit">Đăng nhập</button>
        </form>
        
        <% if(request.getAttribute("errorMessage") != null) { %>
            <p class="error"><%= request.getAttribute("errorMessage") %></p>
        <% } %>
    </div>
</body>
</html>