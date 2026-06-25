<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<%@ page import="vn.edu.hmu.model.EmailAccount" %>
<%@ page import="vn.edu.hmu.model.ITAdmin" %>
<%@ page import="vn.edu.hmu.util.AESUtil" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng thông tin Sinh viên - HMU</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
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

    </style>
</head>
<body>
    <jsp:include page="components/header.jsp" />

    <main class="main-container" style="flex-direction: column; align-items: center; gap: 20px;">
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
                        <span class="info-label">Mật khẩu truy cập ứng dụng (Google/Office365)</span>
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
                            <span id="app-password" class="info-value mono" style="color: #e74c3c; background: #fee2e2; padding: 4px 8px; border-radius: 4px; width: fit-content; filter: blur(5px); user-select: none; transition: filter 0.3s;">
                                <%= appPw %>
                            </span>
                            <button id="toggle-pw" type="button" style="background: none; border: none; cursor: pointer; color: var(--secondary); font-size: 16px; padding: 4px;" onclick="togglePassword()" title="Hiện mật khẩu">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                        <small style="color: #6b7280; font-size: 11px;">(Bạn có thể đổi mật khẩu này sau khi đăng nhập vào hệ thống Google Workspace/Office365 của trường)</small>
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
                Cần hỗ trợ? Trở về <a href="index.jsp">Trang chủ</a> để xem thông tin liên hệ phòng CNTT.
            </div>
        </div>
    </main>

    <footer style="background: #343a40; color: #fff; padding: 20px; text-align: center; margin-top: auto;">
        <p>&copy; 2026 Trường Đại học Y Hà Nội. All rights reserved.</p>
    </footer>

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