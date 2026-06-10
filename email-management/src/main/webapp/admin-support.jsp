<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.edu.hmu.model.SupportRequest" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý yêu cầu hỗ trợ | Admin</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root { --primary: #0056b3; --accent: #3b82f6; --text: #111827; }
        body { font-family: 'Be Vietnam Pro', sans-serif; background-color: #f4f7f6; margin: 0; }
        header { background: #fff; padding: 20px 5%; box-shadow: 0 2px 5px rgba(0,0,0,0.1); display: flex; justify-content: space-between; align-items: center; }
        .container { max-width: 1200px; margin: 30px auto; padding: 20px; }
        h1 { color: var(--primary); }
        table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        th, td { padding: 15px; text-align: left; border-bottom: 1px solid #eee; }
        th { background: #f8f9fa; color: #666; font-weight: 600; }
        .status-pending { color: #f59e0b; font-weight: bold; }
        .status-resolved { color: #10b981; font-weight: bold; }
        .btn { padding: 8px 15px; border-radius: 6px; text-decoration: none; font-size: 13px; font-weight: 600; cursor: pointer; border: none; }
        .btn-resolve { background: #dcfce7; color: #166534; }
        .btn-back { background: #eee; color: #333; margin-bottom: 20px; display: inline-block; }
    </style>
</head>
<body>
    <header>
        <span style="font-weight: 800; color: var(--primary);">HMU Admin Support</span>
        <a href="dashboard" class="btn btn-back">Quay lại Dashboard</a>
    </header>
    <div class="container">
        <h1>Danh sách yêu cầu hỗ trợ</h1>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Sinh viên</th>
                    <th>Chủ đề</th>
                    <th>Nội dung</th>
                    <th>Ngày gửi</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<SupportRequest> requests = (List<SupportRequest>) request.getAttribute("supportRequests");
                    if (requests != null) {
                        for (SupportRequest r : requests) {
                %>
                <tr>
                    <td><%= r.getRequestId() %></td>
                    <td><%= r.getStudentName() %> (<%= r.getStudentId() %>)</td>
                    <td><%= r.getSubject() %></td>
                    <td><%= r.getContent() %></td>
                    <td><%= r.getCreatedAt() %></td>
                    <td>
                        <span class="<%= r.getStatus() == 0 ? "status-pending" : "status-resolved" %>">
                            <%= r.getStatus() == 0 ? "Đang chờ" : "Đã xử lý" %>
                        </span>
                    </td>
                    <td>
                        <% if (r.getStatus() == 0) { %>
                            <a href="admin-support?action=resolve&id=<%= r.getRequestId() %>" class="btn btn-resolve">Xác nhận xử lý</a>
                        <% } %>
                    </td>
                </tr>
                <%
                        }
                    }
                %>
            </tbody>
        </table>
    </div>
</body>
</html>
