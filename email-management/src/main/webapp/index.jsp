<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="vn.edu.hmu.model.EmailAccount" %>
<%@ page import="vn.edu.hmu.model.ITAdmin" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trường Đại học Y Hà Nội - Hệ thống quản trị đại học trực tuyến</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #0056b3;
            --secondary: #6c757d;
            --light: #f8f9fa;
            --dark: #343a40;
            --accent: #3b82f6;
            --text: #111827;
            --bg: #ffffff;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Be Vietnam Pro', sans-serif; line-height: 1.6; color: var(--text); background-color: #f4f7f6; }
        
        /* Header */
        header { background: #fff; padding: 20px 5%; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .logo-section h1 { font-size: 24px; color: var(--primary); font-weight: 800; }
        .login-btn { padding: 10px 20px; background: var(--accent); color: #fff; text-decoration: none; border-radius: 8px; font-weight: 600; transition: 0.3s; }
        .login-btn:hover { background: #2563eb; }

        /* Navigation Taskbar */
        nav { background: var(--primary); color: #fff; padding: 0 5%; }
        nav ul { list-style: none; display: flex; }
        nav ul li { position: relative; }
        nav ul li a { display: block; padding: 15px 20px; color: #fff; text-decoration: none; font-weight: 500; transition: 0.3s; }
        nav ul li a:hover { background: rgba(255,255,255,0.1); }

        /* Main Content */
        .container { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
        .intro-section { background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); margin-bottom: 30px; }
        .intro-section h2 { margin-bottom: 20px; color: var(--primary); }
        
        .image-gallery { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .image-card { background: #eee; height: 200px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-weight: bold; color: #999; overflow: hidden; position: relative; }
        .image-card img { width: 100%; height: 100%; object-fit: cover; }

        .news-section { background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        .news-section h2 { margin-bottom: 20px; color: var(--primary); border-bottom: 2px solid var(--primary); display: inline-block; padding-bottom: 5px; }
        .news-item { margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid #eee; }
        .news-item h3 { font-size: 18px; margin-bottom: 5px; color: var(--text); }
        .news-item p { font-size: 14px; color: var(--secondary); }

        /* Footer */
        footer { background: var(--dark); color: #fff; padding: 40px 5%; text-align: center; margin-top: 50px; }
        
        .user-info { font-size: 14px; color: var(--secondary); margin-right: 15px; }
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
        <section class="intro-section">
            <h2>Giới thiệu về HMU</h2>
            <p>Trường Đại học Y Hà Nội là một trong những trường đại học lâu đời và uy tín nhất tại Việt Nam. Với sứ mạng đào tạo nguồn nhân lực y tế chất lượng cao, nghiên cứu khoa học và chuyển giao công nghệ, trường luôn khẳng định vị thế dẫn đầu trong hệ thống giáo dục y dược nước nhà.</p>
            <p>Hệ thống quản trị đại học trực tuyến được thiết kế nhằm nâng cao hiệu quả quản lý và cung cấp các dịch vụ tiện ích cho sinh viên và cán bộ giảng viên.</p>
        </section>

        <section class="image-gallery">
            <div class="image-card">
                <img src="image/hmu_campus.png" alt="HMU Campus">
                <!-- Fallback if image not found -->
            </div>
            <div class="image-card">
                <img src="image/hmu_library.jpg" alt="HMU Library">
            </div>
            <div class="image-card">
                <img src="image/hmu_laboratory.jpg" alt="HMU Lab">
            </div>
        </section>

        <section class="news-section">
            <h2>Tin tức mới</h2>
            <div class="news-item">
                <h3>Thông báo về kỳ thi tốt nghiệp năm 2026</h3>
                <p>Ngày đăng: 20/05/2026 - Chi tiết về lịch thi, địa điểm và các quy định cần lưu ý...</p>
            </div>
            <div class="news-item">
                <h3>Hội thảo khoa học quốc tế về Y học hiện đại</h3>
                <p>Ngày đăng: 18/05/2026 - Trường Đại học Y Hà Nội phối hợp cùng các chuyên gia quốc tế tổ chức hội thảo...</p>
            </div>
            <div class="news-item">
                <h3>Chương trình trao đổi sinh viên tại Pháp</h3>
                <p>Ngày đăng: 15/05/2026 - Cơ hội học tập và trải nghiệm môi trường y tế chuyên nghiệp tại châu Âu...</p>
            </div>
        </section>
    </div>

    <footer>
        <p>&copy; 2026 Trường Đại học Y Hà Nội. All rights reserved.</p>
        <p>Địa chỉ: Số 1 Tôn Thất Tùng, Đống Đa, Hà Nội</p>
    </footer>

</body>
</html>
