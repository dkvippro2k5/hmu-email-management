<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Ensure user is logged in
    vn.edu.hmu.model.EmailAccount user = (vn.edu.hmu.model.EmailAccount) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi Mật Khẩu Bắt Buộc - HMU</title>
    <!-- CSS -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #004d99;
            --text-color: #333;
            --bg-color: #f5f7fa;
            --white: #ffffff;
            --error-color: #e74c3c;
            --border-radius: 12px;
            --box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            --transition: all 0.3s ease;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--bg-color);
            background-image: linear-gradient(135deg, #f5f7fa 0%, #e6f0fa 100%);
            color: var(--text-color);
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            padding: 20px;
        }

        .password-container {
            background-color: var(--white);
            border-radius: var(--border-radius);
            box-shadow: var(--box-shadow);
            width: 100%;
            max-width: 450px;
            padding: 40px;
        }

        .header {
            text-align: center;
            margin-bottom: 30px;
        }

        .header h2 {
            color: var(--primary-color);
            font-size: 24px;
            margin: 0 0 10px;
        }

        .header p {
            color: #666;
            font-size: 14px;
            margin: 0;
        }

        .form-group {
            margin-bottom: 20px;
            position: relative;
        }

        .form-group label {
            display: block;
            font-size: 14px;
            font-weight: 600;
            color: #4a5568;
            margin-bottom: 8px;
        }

        .form-group input {
            width: 100%;
            padding: 12px 16px 12px 40px;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            font-size: 15px;
            transition: var(--transition);
            box-sizing: border-box;
        }

        .form-group input:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(0, 77, 153, 0.1);
            outline: none;
        }

        .form-group i.icon {
            position: absolute;
            left: 14px;
            top: 38px;
            color: #a0aec0;
            font-size: 16px;
        }

        .btn-submit {
            width: 100%;
            padding: 14px;
            background-color: var(--primary-color);
            color: var(--white);
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition);
        }

        .btn-submit:hover {
            background-color: #003366;
        }

        .alert {
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
            background-color: rgba(231, 76, 60, 0.1);
            color: var(--error-color);
            border: 1px solid rgba(231, 76, 60, 0.2);
            display: none;
        }

        .alert.active {
            display: block;
        }
    </style>
</head>
<body>

<div class="password-container">
    <div class="header">
        <h2>Đổi Mật Khẩu</h2>
        <p>Để đảm bảo an toàn, bạn bắt buộc phải đổi mật khẩu mặc định trong lần đăng nhập đầu tiên.</p>
    </div>

    <div class="alert ${not empty errorMessage ? 'active' : ''}">
        <i class="fas fa-exclamation-circle"></i> ${errorMessage}
    </div>

    <form action="change-password" method="post" id="passwordForm">
        <div class="form-group">
            <label for="newPassword">Mật khẩu mới</label>
            <i class="fas fa-lock icon"></i>
            <input type="password" id="newPassword" name="newPassword" required placeholder="Nhập mật khẩu mới (Ít nhất 8 ký tự)">
        </div>

        <div class="form-group">
            <label for="confirmPassword">Nhập lại mật khẩu</label>
            <i class="fas fa-lock icon"></i>
            <input type="password" id="confirmPassword" name="confirmPassword" required placeholder="Xác nhận lại mật khẩu">
        </div>

        <button type="submit" class="btn-submit">Cập nhật mật khẩu</button>
    </form>

    <form action="change-password" method="post" style="margin-top: 15px;">
        <input type="hidden" name="skip" value="true">
        <button type="submit" class="btn-submit" style="background-color: #f1f5f9; color: #4a5568; border: 1px solid #cbd5e0;">Để sau</button>
    </form>
</div>

<script>
    document.getElementById('passwordForm').addEventListener('submit', function(e) {
        var newPass = document.getElementById('newPassword').value;
        var confirmPass = document.getElementById('confirmPassword').value;
        var alertBox = document.querySelector('.alert');

        if (newPass.length < 8) {
            e.preventDefault();
            alertBox.innerHTML = '<i class="fas fa-exclamation-circle"></i> Mật khẩu phải có ít nhất 8 ký tự.';
            alertBox.classList.add('active');
            return;
        }

        if (newPass !== confirmPass) {
            e.preventDefault();
            alertBox.innerHTML = '<i class="fas fa-exclamation-circle"></i> Mật khẩu nhập lại không khớp.';
            alertBox.classList.add('active');
        }
    });
</script>

</body>
</html>
