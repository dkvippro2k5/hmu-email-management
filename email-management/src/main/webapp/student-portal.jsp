<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<%@ page import="vn.edu.hmu.model.EmailAccount" %>
<%@ page import="vn.edu.hmu.model.ITAdmin" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng thông tin Sinh viên - HMU</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #0056b3;
            --secondary: #6c757d;
            --bg: #f0f2f5;
            --surface: #ffffff;
            --surface2: #f3f4f6;
            --border: #e5e7eb;
            --accent: #3b82f6;
            --accent2: #2563eb;
            --text: #111827;
            --text2: #4b5563;
            --text3: #6b7280;
            --header-h: 64px;
            --radius: 12px;
        }

        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Be Vietnam Pro', 'Arial', sans-serif;
            background: var(--bg);
            color: var(--text);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        /* Standard Header */
        header { background: #fff; padding: 20px 5%; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .logo-section h1 { font-size: 24px; color: var(--primary); font-weight: 800; }
        .login-btn { padding: 10px 20px; background: var(--accent); color: #fff; text-decoration: none; border-radius: 8px; font-weight: 600; transition: 0.3s; }
        .login-btn:hover { background: #2563eb; }
        .user-info { font-size: 14px; color: var(--secondary); margin-right: 15px; }

        /* Standard Navigation Taskbar */
        nav { background: var(--primary); color: #fff; padding: 0 5%; }
        nav ul { list-style: none; display: flex; }
        nav ul li a { display: block; padding: 15px 20px; color: #fff; text-decoration: none; font-weight: 500; transition: 0.3s; }
        nav ul li a:hover { background: rgba(255,255,255,0.1); }

        /* MAIN CONTENT */
        .main-container {
            flex: 1;
            padding: 40px 20px;
            display: flex;
            justify-content: center;
        }

        .portal-card {
            width: 100%;
            max-width: 600px;
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
            animation: slideUp 0.4s ease;
        }

        @keyframes slideUp {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .card-header {
            padding: 32px;
            background: linear-gradient(135deg, #f8fafc, #f1f5f9);
            border-bottom: 1px solid var(--border);
        }

        .card-header h3 {
            font-size: 20px;
            font-weight: 700;
            color: var(--text);
            margin-bottom: 8px;
        }

        .card-header p {
            font-size: 14px;
            color: var(--text3);
        }

        .card-body {
            padding: 32px;
        }

        .info-list {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .info-item {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .info-label {
            font-size: 12px;
            font-weight: 700;
            color: var(--text3);
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }

        .info-value {
            font-size: 16px;
            font-weight: 600;
            color: var(--text);
        }

        .info-value.mono {
            font-family: 'JetBrains Mono', monospace;
            font-size: 15px;
        }

        .email-value {
            color: var(--accent2);
            font-weight: 700;
        }

        .badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            background: rgba(16, 185, 129, 0.1);
            color: #059669;
            border: 1px solid rgba(16, 185, 129, 0.2);
            width: fit-content;
        }

        .badge-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: currentColor;
        }

        .footer {
            padding: 24px 32px;
            background: var(--surface2);
            border-top: 1px solid var(--border);
            text-align: center;
            font-size: 13px;
            color: var(--text3);
        }

        .footer a {
            color: var(--accent);
            text-decoration: none;
            font-weight: 600;
        }

        .footer a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

    <header>
        <div class="logo-section">
            <h1>Trường Đại học Y Hà Nội</h1>
        </div>
        <div>
            <% 
                EmailAccount user = (EmailAccount) session.getAttribute("user");
                ITAdmin admin = (ITAdmin) session.getAttribute("currentAdmin");
                if (user != null || admin != null) {
                    String name = (user != null) ? user.getStudentId() : admin.getFullName();
                    String dashboardLink = (admin != null) ? "dashboard" : "student-portal.jsp";
            %>
                <span class="user-info">Xin chào, <strong><%= name %></strong></span>
                <a href="<%= dashboardLink %>" class="login-btn">Vào hệ thống</a>
                <a href="logout" class="login-btn" style="background-color: #ef4444; margin-left: 10px;">Đăng xuất</a>
            <% } else { %>
                <a href="login.jsp" class="login-btn">Đăng nhập</a>
            <% } %>
        </div>
    </header>

    <nav>
        <ul>
            <li><a href="index.jsp">Trang chủ</a></li>
            <li><a href="admissions.jsp">Tuyển sinh - Đào tạo</a></li>
            <li><a href="it-services">Hệ thống và dịch vụ CNTT</a></li>
            <li><a href="support">Liên hệ hỗ trợ</a></li>
            <% if (user != null) { %>
                <li><a href="personal-info">Thông tin cá nhân</a></li>
                <li><a href="student-portal.jsp">Tài khoản Email</a></li>
            <% } %>
        </ul>
    </nav>

    <main class="main-container">
        <div class="portal-card">
            <div class="card-header">
                <h3>🎓 Thông tin Tài khoản Email</h3>
                <p>Chào mừng bạn quay trở lại. Dưới đây là thông tin tài khoản của bạn.</p>
            </div>
            
            <div class="card-body">
                <div class="info-list">
                    <div class="info-item">
                        <span class="info-label">Mã Sinh viên</span>
                        <span class="info-value mono">${sessionScope.user.studentId}</span>
                    </div>
                    
                    <div class="info-item">
                        <span class="info-label">Địa chỉ Email được cấp</span>
                        <span class="info-value mono email-value">${sessionScope.user.emailAddress}</span>
                    </div>
                    
                    <div class="info-item">
                        <span class="info-label">Trạng thái tài khoản</span>
                        <div class="badge">
                            <span class="badge-dot"></span>
                            Đang hoạt động
                        </div>
                    </div>
                </div>
            </div>

            <div class="footer">
                Cần hỗ trợ? Truy cập <a href="support">Trung tâm trợ giúp IT</a> hoặc gửi yêu cầu.
            </div>
        </div>
    </main>

    <footer style="background: #343a40; color: #fff; padding: 20px; text-align: center; margin-top: auto;">
        <p>&copy; 2026 Trường Đại học Y Hà Nội. All rights reserved.</p>
    </footer>

</body>
</html>