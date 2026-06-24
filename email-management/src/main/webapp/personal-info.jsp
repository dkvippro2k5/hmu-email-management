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
            <h2 style="color: var(--primary); font-size: 20px; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px;">Lịch sử hoạt động tài khoản</h2>
            <% 
                List<ActionLog> logs = (List<ActionLog>) request.getAttribute("studentLogs");
                if (logs != null && !logs.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            %>
                <table style="width: 100%; border-collapse: collapse; font-size: 14px;">
                    <thead>
                        <tr style="background: #f8fafc; text-align: left;">
                            <th style="padding: 12px; border: 1px solid #eee;">Thời gian</th>
                            <th style="padding: 12px; border: 1px solid #eee;">Hành động</th>
                            <th style="padding: 12px; border: 1px solid #eee;">Chi tiết/Lý do</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (ActionLog log : logs) { %>
                            <tr>
                                <td style="padding: 12px; border: 1px solid #eee; color: #666;"><%= sdf.format(log.getActionTime()) %></td>
                                <td style="padding: 12px; border: 1px solid #eee;">
                                    <span style="padding: 4px 8px; border-radius: 4px; font-weight: 600; font-size: 12px; 
                                        <%= log.getActionType().equals("RESET_PASSWORD") ? "background: #dcfce7; color: #166534;" : "background: #f1f5f9; color: #475569;" %>">
                                        <%= log.getActionType() %>
                                    </span>
                                </td>
                                <td style="padding: 12px; border: 1px solid #eee;"><%= log.getReason() %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p style="text-align: center; color: #999; font-style: italic;">Chưa có hoạt động nào được ghi lại.</p>
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