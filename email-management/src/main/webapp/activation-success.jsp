<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="vn.edu.hmu.model.EmailAccount" %>
<%@ page import="vn.edu.hmu.util.AESUtil" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kích hoạt thành công - HMU</title>
    <!-- CSS -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- Inline CSS -->
    <style>
        :root {
            --primary-color: #004d99;
            --success-color: #2ecc71;
            --text-color: #333;
            --bg-color: #f5f7fa;
            --white: #ffffff;
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

        .success-container {
            background-color: var(--white);
            border-radius: var(--border-radius);
            box-shadow: var(--box-shadow);
            width: 100%;
            max-width: 500px;
            padding: 40px;
            text-align: center;
        }

        .icon-success {
            font-size: 64px;
            color: var(--success-color);
            margin-bottom: 20px;
        }

        .success-header h2 {
            color: var(--primary-color);
            font-size: 24px;
            font-weight: 700;
            margin: 0 0 10px;
        }

        .success-header p {
            color: #666;
            font-size: 15px;
            margin: 0 0 30px;
            line-height: 1.6;
        }

        .account-details {
            background-color: #f8fafc;
            border: 2px dashed #cbd5e0;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            text-align: left;
        }

        .account-row {
            margin-bottom: 15px;
        }
        
        .account-row:last-child {
            margin-bottom: 0;
        }

        .account-label {
            display: block;
            font-size: 13px;
            color: #718096;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 5px;
        }

        .account-value {
            font-size: 18px;
            color: var(--primary-color);
            font-weight: 700;
            word-break: break-all;
        }

        .btn-primary {
            display: inline-block;
            width: 100%;
            padding: 14px;
            background-color: var(--primary-color);
            color: var(--white);
            text-decoration: none;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            transition: var(--transition);
        }

        .btn-primary:hover {
            background-color: #003366;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 77, 153, 0.2);
        }
    </style>
</head>
<body>

<div class="success-container">
    <i class="fas fa-check-circle icon-success"></i>
    <div class="success-header">
        <h2>Kích Hoạt Thành Công!</h2>
        <p>Tài khoản của bạn đã được hệ thống phê duyệt và cấp phát. Dưới đây là thông tin đăng nhập chính thức của bạn.</p>
    </div>

    <div class="account-details">
        <div class="account-row">
            <span class="account-label">Tài Khoản Email HMU</span>
            <div class="account-value">${sessionScope.user.emailAddress}</div>
        </div>
        <div class="account-row">
            <span class="account-label">Mật khẩu được cấp</span>
            <div style="display: flex; align-items: center; gap: 8px;">
<%
    String appPw = "";
    if (session.getAttribute("user") != null) {
        vn.edu.hmu.model.EmailAccount currentUser = (vn.edu.hmu.model.EmailAccount)session.getAttribute("user");
        appPw = "Hmu@" + currentUser.getStudentId();
        if (currentUser.getInitialPasswordEncrypted() != null && !currentUser.getInitialPasswordEncrypted().isEmpty()) {
            try {
                String decrypted = vn.edu.hmu.util.AESUtil.decrypt(currentUser.getInitialPasswordEncrypted());
                if (decrypted != null) {
                    appPw = decrypted;
                }
            } catch (Exception ignore) {}
        }
    }
%>
                <div id="app-password" class="account-value" style="filter: blur(5px); user-select: none; transition: filter 0.3s; margin-bottom: 0;">
                    <%= appPw %>
                </div>
                <button id="toggle-pw" type="button" style="background: none; border: none; cursor: pointer; color: #3b82f6; font-size: 16px; padding: 4px;" onclick="togglePassword()" title="Hiện mật khẩu">
                    <i class="fas fa-eye"></i>
                </button>
            </div>
        </div>
    </div>

    <p style="font-size: 13px; color: #e74c3c; margin-bottom: 20px;">
        <i class="fas fa-shield-alt"></i> Mật khẩu này được sử dụng cho các ứng dụng của trường. Bạn có thể thay đổi sau.
    </p>

    <!-- Nút đi tới portal trực tiếp vì đang có session -->
    <a href="student-portal.jsp" class="btn-primary">
        <i class="fas fa-arrow-right"></i> Đi tới Dashboard
    </a>
</div>

<script>
    function togglePassword() {
        var pw = document.getElementById("app-password");
        var btn = document.getElementById("toggle-pw");
        if (pw.style.filter === "none") {
            pw.style.filter = "blur(5px)";
            pw.style.userSelect = "none";
            btn.innerHTML = '<i class="fas fa-eye"></i>';
            btn.title = "Hiện mật khẩu";
        } else {
            pw.style.filter = "none";
            pw.style.userSelect = "auto";
            btn.innerHTML = '<i class="fas fa-eye-slash"></i>';
            btn.title = "Ẩn mật khẩu";
        }
    }
</script>

</body>
</html>
