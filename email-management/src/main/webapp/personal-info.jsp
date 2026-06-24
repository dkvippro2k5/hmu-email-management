<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="vn.edu.hmu.model.Student" %>
<%@ page import="vn.edu.hmu.model.EmailAccount" %>
<%@ page import="vn.edu.hmu.model.ITAdmin" %>
<%@ page import="vn.edu.hmu.model.ActionLog" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin cá nhân | HMU</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root { --primary: #0056b3; --accent: #3b82f6; --text: #111827; --secondary: #6c757d; }
        body { font-family: 'Be Vietnam Pro', sans-serif; line-height: 1.6; color: var(--text); background-color: #f4f7f6; margin: 0; overflow-y: scroll; }
        
        .container { max-width: 800px; margin: 30px auto; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        h1 { color: var(--primary); margin-bottom: 30px; text-align: center; }
        .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .info-group { margin-bottom: 15px; }
        .info-group label { display: block; font-size: 13px; font-weight: 700; color: #666; margin-bottom: 5px; text-transform: uppercase; }
        .info-group p { font-size: 16px; color: var(--text); background: #f9fafb; padding: 10px; border-radius: 8px; border: 1px solid #eee; }
        footer { background: #343a40; color: #fff; padding: 20px; text-align: center; margin-top: 50px; }
        .error-message { text-align: center; padding: 50px; color: #ef4444; font-weight: 600; }
    </style>
</head>
<body>
    <jsp:include page="components/header.jsp" />
    <div class="container">
        <h1>Thông tin cá nhân</h1>
        <% 
            Student s = (Student) request.getAttribute("student"); 
            if (s != null) {
        %>
        <div class="info-grid">
            <div class="info-group">
                <label>Mã sinh viên</label>
                <p><%= s.getStudentId() %></p>
            </div>
            <div class="info-group">
                <label>Họ và tên</label>
                <p><%= s.getFullName() %></p>
            </div>
            <div class="info-group">
                <label>CCCD / CMND</label>
                <p><%= s.getCccd() != null ? s.getCccd() : "Chưa cập nhật" %></p>
            </div>
            <div class="info-group">
                <label>Số điện thoại</label>
                <p><%= s.getPhoneNumber() != null ? s.getPhoneNumber() : "Chưa cập nhật" %></p>
            </div>
            <div class="info-group">
                <label>Niên Khóa</label>
                <p><%= s.getCohort() != null ? s.getCohort() : "Chưa cập nhật" %></p>
            </div>
            <div class="info-group">
                <label>Trạng thái</label>
                <p>Hoạt động</p>
            </div>
        </div>

        <div style="margin-top: 50px;">
<%@ page import="vn.edu.hmu.model.Notification" %>
            <h2 style="color: var(--primary); font-size: 20px; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px;">Thông báo từ hệ thống</h2>
            <% 
                List<Notification> notifs = (List<Notification>) request.getAttribute("studentNotifs");
                if (notifs != null && !notifs.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            %>
                <table style="width: 100%; border-collapse: collapse; font-size: 14px;">
                    <thead>
                        <tr style="background: #f8fafc; text-align: left;">
                            <th style="padding: 12px; border: 1px solid #eee;">Thời gian</th>
                            <th style="padding: 12px; border: 1px solid #eee;">Tiêu đề</th>
                            <th style="padding: 12px; border: 1px solid #eee;">Nội dung</th>
                            <th style="padding: 12px; border: 1px solid #eee; width: 100px; text-align: center;">Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Notification notif : notifs) { %>
                            <tr style="<%= notif.isRead() ? "" : "background-color: #f0fdf4; font-weight: 500;" %>">
                                <td style="padding: 12px; border: 1px solid #eee; color: #666; width: 150px;"><%= sdf.format(notif.getCreatedAt()) %></td>
                                <td style="padding: 12px; border: 1px solid #eee; color: var(--primary);"><%= notif.getTitle() %></td>
                                <td style="padding: 12px; border: 1px solid #eee;"><%= notif.getMessage() %></td>
                                <td style="padding: 12px; border: 1px solid #eee; text-align: center;">
                                    <% if (notif.isRead()) { %>
                                        <span style="color: #94a3b8; font-size: 12px;">Đã đọc</span>
                                    <% } else { %>
                                        <span style="color: #10b981; font-size: 12px; font-weight: bold;">Mới</span>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p style="text-align: center; color: #999; font-style: italic;">Chưa có thông báo nào.</p>
            <% } %>
        </div>
        <% } else { %>
            <div class="error-message">
                <p>Không tìm thấy thông tin sinh viên. Vui lòng liên hệ quản trị viên.</p>
                <a href="index.jsp" class="login-btn" style="margin-top: 20px; display: inline-block;">Quay lại trang chủ</a>
            </div>
        <% } %>
    </div>
    <footer>
        <p>&copy; 2026 Trường Đại học Y Hà Nội. All rights reserved.</p>
    </footer>
</body>
</html>