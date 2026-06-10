<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kích hoạt tài khoản - HMU</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg: #f0f2f5;
            --surface: #ffffff;
            --border: #e5e7eb;
            --border2: #d1d5db;
            --accent: #3b82f6;
            --accent2: #2563eb;
            --accent-glow: rgba(59,130,246,0.25);
            --text: #111827;
            --text2: #4b5563;
            --text3: #6b7280;
            --green: #10b981;
            --radius: 12px;
        }

        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Be Vietnam Pro', 'Arial', sans-serif;
            background: var(--bg);
            color: var(--text);
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 16px;
            width: 100%;
            max-width: 440px;
            padding: 40px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
            animation: fadeIn 0.5s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .header {
            text-align: center;
            margin-bottom: 32px;
        }

        .logo {
            width: 48px;
            height: 48px;
            background: var(--accent);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 24px;
            font-weight: 800;
            margin: 0 auto 16px;
        }

        h2 {
            font-size: 24px;
            font-weight: 700;
            color: var(--text);
            margin-bottom: 8px;
        }

        .subtitle {
            font-size: 14px;
            color: var(--text3);
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
            margin-bottom: 20px;
        }

        label {
            font-size: 13px;
            font-weight: 600;
            color: var(--text2);
        }

        .form-control {
            background: var(--surface);
            border: 1px solid var(--border2);
            color: var(--text);
            border-radius: 8px;
            padding: 12px 14px;
            font-size: 14px;
            outline: none;
            transition: 0.2s;
            font-family: inherit;
        }

        .form-control:focus {
            border-color: var(--accent);
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
        }

        hr {
            border: 0;
            border-top: 1px solid var(--border);
            margin: 24px 0;
        }

        .btn-submit {
            width: 100%;
            background: var(--accent);
            color: #fff;
            border: none;
            border-radius: 8px;
            padding: 14px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
            font-family: inherit;
            margin-top: 8px;
        }

        .btn-submit:hover {
            background: var(--accent2);
            box-shadow: 0 4px 12px var(--accent-glow);
            transform: translateY(-1px);
        }

        .btn-submit:active {
            transform: translateY(0);
        }

        .note {
            font-size: 12px;
            color: var(--text3);
            text-align: center;
            margin-top: 20px;
            line-height: 1.5;
        }

        .info-box {
            background: rgba(59, 130, 246, 0.05);
            border: 1px solid rgba(59, 130, 246, 0.1);
            border-radius: 8px;
            padding: 12px;
            margin-bottom: 24px;
            font-size: 13px;
            color: var(--accent2);
            display: flex;
            gap: 10px;
            align-items: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="logo">HM</div>
            <h2>Kích hoạt tài khoản</h2>
            <p class="subtitle">Chào mừng bạn đến với hệ thống Email HMU</p>
        </div>

        <div class="info-box">
            <span>ℹ️</span>
            <span>Vui lòng thiết lập mật khẩu mới và thông tin liên hệ để bắt đầu sử dụng.</span>
        </div>
        
        <form action="activate-account" method="POST">
            <div class="form-group">
                <label>Mật khẩu mới</label>
                <input type="password" name="newPassword" class="form-control" placeholder="Tối thiểu 6 ký tự" required minlength="6">
            </div>
            <div class="form-group">
                <label>Xác nhận mật khẩu</label>
                <input type="password" name="confirmPassword" class="form-control" placeholder="Nhập lại mật khẩu mới" required>
            </div>
            
            <hr>
            
            <div class="form-group">
                <label>Số điện thoại liên hệ</label>
                <input type="text" name="phone" class="form-control" placeholder="VD: 0912345678" required>
            </div>
            
            <button type="submit" class="btn-submit">Xác nhận & Kích hoạt</button>
            <p class="note">Đây là quy định bắt buộc trong lần đăng nhập đầu tiên để bảo mật tài khoản của bạn.</p>
        </form>
    </div>
</body>
</html>