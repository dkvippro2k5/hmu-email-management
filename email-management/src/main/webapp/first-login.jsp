<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Kích hoạt tài khoản - HMU</title>
    <link rel="stylesheet" href="css/first-login.css">
</head>
<body>
    <div class="container">
        <h2>Kích hoạt tài khoản</h2>
        <p style="text-align: center; font-size: 14px;">Chào mừng bạn! Vui lòng đổi mật khẩu để tiếp tục.</p>
        
        <form action="activate-account" method="POST">
            <div class="form-group">
                <label>Mật khẩu mới:</label>
                <input type="password" name="newPassword" required minlength="6">
            </div>
            <div class="form-group">
                <label>Xác nhận mật khẩu:</label>
                <input type="password" name="confirmPassword" required>
            </div>
            <hr>
            <div class="form-group">
                <label>Số điện thoại liên hệ:</label>
                <input type="text" name="phone" required>
            </div>
            
            <button type="submit" class="btn-submit">Xác nhận & Kích hoạt</button>
            <p class="note">* Đây là quy định bắt buộc trong lần đăng nhập đầu tiên.</p>
        </form>
    </div>
</body>
</html>