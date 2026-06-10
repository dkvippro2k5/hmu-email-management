<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="vn.edu.hmu.model.EmailAccount" %>
<%@ page import="vn.edu.hmu.model.ITAdmin" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tuyển sinh - Đào tạo | HMU</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root { --primary: #0056b3; --accent: #3b82f6; --text: #111827; --secondary: #6c757d; }
        body { font-family: 'Be Vietnam Pro', sans-serif; line-height: 1.6; color: var(--text); background-color: #f4f7f6; margin: 0; }
        
        /* Header */
        header { background: #fff; padding: 20px 5%; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .logo-section h1 { font-size: 24px; color: var(--primary); font-weight: 800; }
        .login-btn { padding: 10px 20px; background: var(--accent); color: #fff; text-decoration: none; border-radius: 8px; font-weight: 600; transition: 0.3s; }
        .login-btn:hover { background: #2563eb; }
        .user-info { font-size: 14px; color: var(--secondary); margin-right: 15px; }

        /* Navigation Taskbar */
        nav { background: var(--primary); color: #fff; padding: 0 5%; }
        nav ul { list-style: none; display: flex; }
        nav ul li a { display: block; padding: 15px 20px; color: #fff; text-decoration: none; font-weight: 500; transition: 0.3s; }
        nav ul li a:hover { background: rgba(255,255,255,0.1); }

        .container { max-width: 1000px; margin: 30px auto; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        h1 { color: var(--primary); margin-bottom: 20px; }
        h2 { color: var(--accent); margin-top: 30px; margin-bottom: 10px; }
        p { margin-bottom: 15px; }
        footer { background: #343a40; color: #fff; padding: 20px; text-align: center; margin-top: 50px; }
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
    <div class="container">
        <h1>Tuyển sinh - Đào tạo</h1>
        
        <h2>Tuyển sinh Đại học</h2>
        <p>Thông tin về các ngành đào tạo y khoa, răng hàm mặt, dược học, điều dưỡng, kỹ thuật y học, y tế công cộng và dinh dưỡng.</p>
        <p>Phương thức xét tuyển năm 2026: Xét tuyển dựa trên kết quả kỳ thi tốt nghiệp THPT và kết quả học tập THPT.</p>
        
        <h2>Đào tạo Sau đại học</h2>
        <p>Các chương trình đào tạo Bác sĩ nội trú, Cao học, Nghiên cứu sinh và Chuyên khoa cấp I, cấp II.</p>
        
        <h2>Đào tạo liên tục</h2>
        <p>Cung cấp các khóa đào tạo ngắn hạn, cập nhật kiến thức y khoa cho cán bộ y tế đang công tác.</p>
    </div>
    <footer>
        <p>&copy; 2026 Trường Đại học Y Hà Nội. All rights reserved.</p>
    </footer>
</body>
</html>