<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu | HMU</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root { --primary: #0056b3; --accent: #3b82f6; --text: #111827; --secondary: #6c757d; }
        body { font-family: 'Be Vietnam Pro', sans-serif; background-color: #f4f7f6; display: flex; align-items: center; justify-content: center; min-height: 100vh; margin: 0; }
        .login-card { background: #fff; padding: 40px; border-radius: 16px; box-shadow: 0 10px 25px rgba(0,0,0,0.05); width: 100%; max-width: 400px; }
        .logo-section { text-align: center; margin-bottom: 30px; }
        .logo-section h1 { font-size: 24px; color: var(--primary); font-weight: 800; margin: 0; }
        .logo-section p { color: var(--secondary); font-size: 14px; margin-top: 5px; }
        
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; font-size: 14px; font-weight: 600; margin-bottom: 8px; }
        .form-control { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-family: inherit; box-sizing: border-box; }
        
        .btn-submit { width: 100%; padding: 12px; background: var(--accent); color: #fff; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; transition: 0.3s; font-size: 16px; }
        .btn-submit:hover { background: #2563eb; }
        
        .alert { padding: 12px; border-radius: 8px; margin-bottom: 20px; font-size: 14px; }
        .alert-success { background: #dcfce7; color: #166534; border: 1px solid #bbf7d0; }
        .alert-danger { background: #fee2e2; color: #991b1b; border: 1px solid #fecaca; }
        
        .back-link { text-align: center; margin-top: 20px; font-size: 14px; }
        .back-link a { color: var(--accent); text-decoration: none; font-weight: 600; }
    </style>
</head>
<body>
    <div class="login-card">
        <div class="logo-section">
            <h1>HMU IT Services</h1>
            <p>Khôi phục mật khẩu tài khoản</p>
        </div>

        <% if (request.getAttribute("message") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("message") %></div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="forgot-password" method="post">
            <div class="form-group">
                <label>Email sinh viên (@hmu.edu.vn)</label>
                <input type="email" name="email" class="form-control" placeholder="vidu: nguyenvana@hmu.edu.vn" required>
            </div>
            <button type="submit" class="btn-submit">Gửi yêu cầu khôi phục</button>
        </form>

        <div class="back-link">
            <a href="login.jsp">← Quay lại đăng nhập</a>
        </div>
    </div>
</body>
</html>
