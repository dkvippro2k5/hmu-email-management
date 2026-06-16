<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="vn.edu.hmu.model.EmailAccount" %>
<%@ page import="vn.edu.hmu.model.ITAdmin" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ thống và dịch vụ CNTT | HMU</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root { --primary: #0056b3; --accent: #3b82f6; --text: #111827; --secondary: #6c757d; }
        body { font-family: 'Be Vietnam Pro', sans-serif; line-height: 1.6; color: var(--text); background-color: #f4f7f6; margin: 0; overflow-y: scroll; }
        
        .container { max-width: 1000px; margin: 30px auto; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        h1 { color: var(--primary); margin-bottom: 30px; }
        .service-list { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; }
        .service-card { border: 1px solid #eee; padding: 20px; border-radius: 12px; transition: 0.3s; text-decoration: none; color: inherit; display: block; }
        .service-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.05); border-color: var(--accent); }
        .service-card h3 { color: var(--primary); margin-bottom: 10px; }
        .service-card p { font-size: 14px; color: #666; }
        footer { background: #343a40; color: #fff; padding: 20px; text-align: center; margin-top: 50px; }
    </style>
</head>
<body>
    <jsp:include page="components/header.jsp" />
    <div class="container">
        <h1>Hệ thống và dịch vụ CNTT</h1>
        <p style="margin-bottom: 30px;">Chào mừng bạn đến với cổng dịch vụ CNTT của Trường Đại học Y Hà Nội. Vui lòng chọn dịch vụ bạn muốn truy cập:</p>
        
        <div class="service-list">
            <a href="https://portal.hmu.vn" class="service-card" onclick="return checkLogin(event, this.href)">
                <h3>Cổng thông tin sinh viên</h3>
                <p>Xem điểm, đăng ký học phần và các thủ tục hành chính trực tuyến.</p>
            </a>
            <a href="http://baigiang.hmu.vn" class="service-card" onclick="return checkLogin(event, this.href)">
                <h3>Hệ thống bài giảng điện tử</h3>
                <p>Truy cập tài liệu học tập, slide bài giảng và các khóa học online.</p>
            </a>
            <a href="https://thuvienykhoa.hmu.vn" class="service-card" onclick="return checkLogin(event, this.href)">
                <h3>Thư viện Y khoa</h3>
                <p>Tra cứu sách, luận văn, luận án và các cơ sở dữ liệu y học quốc tế.</p>
            </a>
        </div>
    </div>

    <script>
        function checkLogin(event, url) {
            const isLoggedIn = <%= (session.getAttribute("user") != null || session.getAttribute("currentAdmin") != null) %>;
            if (!isLoggedIn) {
                event.preventDefault();
                alert("Vui lòng đăng nhập để truy cập nền tảng này của nhà trường.");
                window.location.href = "login.jsp";
                return false;
            }
            window.open(url, '_blank');
            return false;
        }
    </script>
    <footer>
        <p>&copy; 2026 Trường Đại học Y Hà Nội. All rights reserved.</p>
    </footer>
</body>
</html>