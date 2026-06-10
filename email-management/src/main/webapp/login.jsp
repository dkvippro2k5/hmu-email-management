<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - HMU Email Management</title>
    
    <!-- Đồng bộ Font chữ với Dashboard -->
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    
    <style>
        /* ================= BIẾN CSS (ĐỒNG BỘ VỚI DASHBOARD) ================= */
        :root {
            --bg: #f0f2f5;             /* Nền tổng thể */
            --surface: #ffffff;        /* Nền form đăng nhập */
            --border: #e5e7eb;         /* Viền input */
            --border-focus: #3b82f6;   /* Viền input khi focus */
            --accent: #3b82f6;         /* Màu nút chính */
            --accent-hover: #2563eb;   /* Màu nút khi hover */
            --accent-glow: rgba(59,130,246,0.25);
            --text: #111827;           /* Chữ chính */
            --text2: #4b5563;          /* Chữ nhãn (Label) */
            --text3: #6b7280;          /* Chữ phụ, mờ */
            --red-bg: #fef2f2;         /* Nền thông báo lỗi */
            --red-text: #dc2626;       /* Chữ thông báo lỗi */
            --radius: 12px;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Be Vietnam Pro', sans-serif;
            background-color: var(--bg);
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            color: var(--text);
        }

        .login-wrapper {
            background: var(--surface);
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
            width: 100%;
            max-width: 400px;
            text-align: center;
        }

        /* Header Form */
        .login-header {
            margin-bottom: 30px;
        }
        
        .logo-box {
            width: 48px;
            height: 48px;
            background: var(--accent);
            color: #fff;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            font-weight: 800;
            margin: 0 auto 16px auto;
            box-shadow: 0 4px 12px var(--accent-glow);
        }

        .login-header h2 {
            font-size: 22px;
            font-weight: 700;
            margin-bottom: 6px;
        }

        .login-header p {
            font-size: 13px;
            color: var(--text3);
        }

        /* Form Inputs */
        .form-group {
            text-align: left;
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            color: var(--text2);
            margin-bottom: 8px;
        }

        .form-control {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid var(--border);
            border-radius: 8px;
            font-size: 14px;
            font-family: inherit;
            color: var(--text);
            background-color: #f9fafb;
            transition: all 0.2s ease;
            outline: none;
        }

        .form-control::placeholder {
            color: #9ca3af;
        }

        .form-control:focus {
            border-color: var(--border-focus);
            background-color: #ffffff;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
        }

        /* Submit Button */
        .btn-submit {
            width: 100%;
            padding: 14px;
            background-color: var(--accent);
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14.5px;
            font-weight: 700;
            font-family: inherit;
            transition: all 0.2s ease;
            margin-top: 10px;
        }

        .btn-submit:hover {
            background-color: var(--accent-hover);
            box-shadow: 0 6px 16px var(--accent-glow);
            transform: translateY(-1px);
        }

        /* Error Message */
        .error-msg {
            margin-top: 20px;
            padding: 12px;
            background-color: var(--red-bg);
            color: var(--red-text);
            border: 1px solid rgba(239, 68, 68, 0.2);
            border-radius: 8px;
            font-size: 13px;
            font-weight: 500;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
        }
    </style>
</head>
<body>

    <div class="login-wrapper">
        <div class="login-header">
            <div class="logo-box">IT</div>
            <h2>HMU Portal</h2>
            <p>Hệ thống Quản lý Tài khoản Email</p>
        </div>

        <form action="login" method="post">
            <div class="form-group">
                <label>Tên đăng nhập / Mã sinh viên</label>
                <input type="text" name="username" class="form-control" placeholder="Nhập tài khoản của bạn..." required autocomplete="off">
            </div>
            
            <div class="form-group">
                <label>Mật khẩu</label>
                <input type="password" name="password" class="form-control" placeholder="••••••••" required>
            </div>
            
            <button type="submit" class="btn-submit">Đăng nhập</button>
        </form>
        
        <div style="margin-top: 20px; font-size: 13px;">
            <a href="forgot-password" style="color: var(--accent); text-decoration: none; font-weight: 600;">Quên mật khẩu?</a>
        </div>
        
        <div style="margin-top: 15px; border-top: 1px solid var(--border); padding-top: 15px; font-size: 13px;">
            <a href="index.jsp" style="color: var(--text3); text-decoration: none;">← Quay lại Trang chủ</a>
        </div>
        
        <!-- Hiển thị thông báo lỗi từ Backend (nếu có) -->
        <% if(request.getAttribute("errorMessage") != null) { %>
            <div class="error-msg">
                <span>⚠️</span> <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
    </div>

</body>
</html>