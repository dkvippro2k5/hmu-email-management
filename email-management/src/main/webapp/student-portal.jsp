<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cổng thông tin Sinh viên - HMU</title>
    <link rel="stylesheet" href="css/student-portal.css">
</head>
<body>

    <div class="header">
        <h2>🎓 HMU Student Portal</h2>
        <a href="login.jsp" class="btn-logout">Đăng xuất</a>
    </div>

    <div class="container">
        <h3 style="color: #0f4c75; margin-top: 0;">Thông tin Tài khoản Email</h3>
        <p style="color: gray; font-style: italic;">Đây là không gian dành riêng cho sinh viên.</p>
        
        <div class="info-group">
            <span>Mã Sinh viên:</span> ${sessionScope.user.studentId}
        </div>
        <div class="info-group">
            <span>Địa chỉ Email:</span> <b style="color: #0f4c75;">${sessionScope.user.emailAddress}</b>
        </div>
        <div class="info-group">
            <span>Trạng thái:</span> <span class="status-badge">Đang hoạt động</span>
        </div>
    </div>

</body>
</html>