<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="vn.edu.hmu.model.EmailAccount" %>
<%@ page import="vn.edu.hmu.model.ITAdmin" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liên hệ hỗ trợ | HMU</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root { --primary: #0056b3; --accent: #3b82f6; --text: #111827; --secondary: #6c757d; }
        body { font-family: 'Be Vietnam Pro', sans-serif; line-height: 1.6; color: var(--text); background-color: #f4f7f6; margin: 0; overflow-y: scroll; }
        
        .container { max-width: 1000px; margin: 30px auto; display: grid; grid-template-columns: 1fr 2fr; gap: 30px; }
        .contact-info { background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        .support-form { background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        h2 { color: var(--primary); margin-bottom: 20px; }
        .info-item { margin-bottom: 20px; }
        .info-item h4 { color: #666; font-size: 14px; margin-bottom: 5px; }
        .info-item p { font-weight: 600; color: var(--text); }
        
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; font-size: 14px; font-weight: 600; margin-bottom: 8px; }
        .form-control { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-family: inherit; }
        textarea.form-control { resize: vertical; min-height: 150px; }
        .btn-submit { padding: 12px 30px; background: var(--accent); color: #fff; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; transition: 0.3s; }
        .btn-submit:hover { background: #2563eb; }
        
        .alert { padding: 15px; border-radius: 8px; margin-bottom: 20px; }
        .alert-success { background: #dcfce7; color: #166534; border: 1px solid #bbf7d0; }
        .alert-danger { background: #fee2e2; color: #991b1b; border: 1px solid #fecaca; }
        
        footer { background: #343a40; color: #fff; padding: 20px; text-align: center; margin-top: 50px; }
    </style>
</head>
<body>
    <jsp:include page="components/header.jsp" />
    <div class="container">
        <aside class="contact-info">
            <h2>Thông tin liên hệ</h2>
            <div class="info-item">
                <h4>Email Quản trị viên</h4>
                <p>admin.it@hmu.edu.vn</p>
            </div>
            <div class="info-item">
                <h4>Số điện thoại hỗ trợ</h4>
                <p>024.38523798 (Ext: 123)</p>
            </div>
            <div class="info-item">
                <h4>Văn phòng</h4>
                <p>Tầng 2, Nhà A1, HMU</p>
            </div>
        </aside>
        
        <main class="support-form">
            <h2>Gửi yêu cầu hỗ trợ</h2>
            
            <% if (session.getAttribute("user") != null || session.getAttribute("currentAdmin") != null) { %>
                <% if (request.getAttribute("message") != null) { %>
                    <div class="alert alert-success"><%= request.getAttribute("message") %></div>
                <% } %>
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                <% } %>

                <form action="support" method="post">
                    <div class="form-group">
                        <label>Chủ đề yêu cầu</label>
                        <select name="subject" class="form-control" required>
                            <option value="">-- Chọn chủ đề --</option>
                            <option value="Khiếu nại">Khiếu nại</option>
                            <option value="Hỗ trợ kĩ thuật">Hỗ trợ kĩ thuật</option>
                            <option value="Cấp lại mật khẩu">Cấp lại mật khẩu</option>
                            <option value="Khác">Khác</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Nội dung chi tiết</label>
                        <textarea name="content" class="form-control" placeholder="Mô tả chi tiết vấn đề bạn đang gặp phải..." required></textarea>
                    </div>
                    <button type="submit" class="btn-submit">Gửi yêu cầu</button>
                </form>
            <% } else { %>
                <div class="alert alert-danger">
                    Vui lòng <a href="login.jsp" style="color: inherit; font-weight: 700;">đăng nhập</a> để gửi yêu cầu hỗ trợ kĩ thuật.
                </div>
                <p style="color: #666; font-size: 14px;">Bạn vẫn có thể liên hệ với chúng tôi qua các kênh thông tin bên trái nếu gặp sự cố khi đăng nhập.</p>
            <% } %>
        </main>
    </div>
    <footer>
        <p>&copy; 2026 Trường Đại học Y Hà Nội. All rights reserved.</p>
    </footer>
</body>
</html>