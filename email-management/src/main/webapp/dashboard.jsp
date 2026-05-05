<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Student Email Management - HMU</title>
    <!-- Đã nhúng CSS chuẩn -->
    <link rel="stylesheet" href="css/dashboard.css">
</head>
<body>

    <div class="sidebar">
        <div class="sidebar-header">IT ADMIN PORTAL</div>
        
        <div class="menu-title">Nghiệp vụ Email</div>
        <a href="#" class="menu-item active">Quản lý Khởi tạo (FR-01)</a>
        <a href="#" class="menu-item">Quản lý Bảo lưu (FR-02)</a>
        <a href="#" class="menu-item">Quản lý Thu hồi (FR-03)</a>
        
        <div class="menu-title">Hệ thống</div>
        <a href="#" class="menu-item">Nhật ký hoạt động (Log)</a>
        <a href="#" class="menu-item">Báo cáo Thống kê</a>
        
        <a href="login.jsp" class="logout-btn">Đăng xuất</a>
    </div>

    <div class="main-content">
        <div class="topbar">
            <h2>Danh sách Tài khoản Sinh viên</h2>
            <div class="user-info">Cán bộ phụ trách: <b>Admin IT</b></div>
        </div>

        <c:if test="${not empty sessionScope.successMsg}">
            <div style="background-color: #d4edda; color: #155724; padding: 15px 25px; font-weight: bold; border-bottom: 1px solid #c3e6cb;">
                ${sessionScope.successMsg}
            </div>
            <c:remove var="successMsg" scope="session"/>
        </c:if>
        
        <div class="action-bar">
            <div style="display: flex; gap: 10px; align-items: center;">
                <form action="import-students" method="POST" enctype="multipart/form-data" style="display: flex; gap: 10px; margin: 0;">
                    <input type="file" name="excelFile" accept=".xlsx" required 
                        style="padding: 6px; border: 1px solid #ccc; border-radius: 5px;">
                    <button type="submit" class="btn btn-primary">📁 Import Excel (M.01)</button>
                </form>

                <!-- Nút Đồng bộ SSO -->
                <button onclick="syncSSOData()" class="btn" style="background-color: #ff9800; color: white; border: none; padding: 10px 15px; border-radius: 5px; cursor: pointer; font-weight: bold;">
                    🔄 Đồng bộ hệ thống (SSO)
                </button>
                
                <!-- Khu vực hiển thị Loading (Ẩn mặc định) -->
                <div id="apiLoading" style="display:none; margin-left: 10px; align-items: center; gap: 8px; color: #ff9800; font-weight: bold;">
                    <div style="border: 3px solid #f3f3f3; border-top: 3px solid #ff9800; border-radius: 50%; width: 16px; height: 16px; animation: spin 1s linear infinite;"></div>
                    Đang đồng bộ...
                </div>
            </div>
            <!-- Cụm Tìm kiếm (Gồm ô nhập và nút bấm) -->
            <div style="display: flex; gap: 5px; align-items: center;">
                <input type="text" id="searchInput" class="search-box" 
                    placeholder="Tìm kiếm theo Mã SV, Họ tên..." 
                    onkeyup="searchStudent()">
                    
                <!-- Nút Tìm kiếm (Cũng gọi hàm searchStudent) -->
                <button onclick="searchStudent()" class="btn btn-primary" style="padding: 10px 15px;">
                    🔍 
                </button>
                <!-- Bộ lọc Trạng thái mới thêm -->
                <select id="statusFilter" class="search-box" style="width: 200px;" onchange="searchStudent()">
                    <option value="-1">-- Tất cả trạng thái --</option>
                    <option value="0">Chờ kích hoạt</option>
                    <option value="1">Hoạt động</option>
                    <option value="2">Đang bảo lưu</option>
                    <option value="3">Chờ xóa</option>
                </select>
            </div>
        </div>

        <div class="content-area">
            <div class="card">
                <table>
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>Họ và tên</th>
                            <th>Mã SV</th>
                            <th>Email được cấp</th>
                            <th>Ngày kích hoạt</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody id="studentTableBody">
                        <c:forEach items="${dsTaiKhoan}" var="acc" varStatus="loop">
                            <tr>
                                <td>${loop.index + 1}</td>
                                <td>${acc.studentName}</td>
                                <td>${acc.studentId}</td>
                                <td>${acc.emailAddress}</td>
                                <td>${acc.activationDate}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${acc.status == 0}">
                                            <span class="status stt-pending">Chờ kích hoạt</span>
                                        </c:when>
                                        <c:when test="${acc.status == 1}">
                                            <span class="status stt-active">Hoạt động</span>
                                        </c:when>
                                        <c:when test="${acc.status == 2}">
                                            <span class="status stt-suspended">Đã bảo lưu</span>
                                        </c:when>
                                        <c:when test="${acc.status == 3}">
                                            <span class="status stt-revoking">Chờ xóa</span>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td class="action-links">
                                    <!-- Chỉ giữ lại đúng 1 nút Khóa và 1 nút Xóa -->
                                    <a href="javascript:void(0)" 
                                        onclick="suspendAccount('${acc.studentId}')" 
                                        style="${acc.status == 2 ? 'color: gray; pointer-events: none;' : ''}">
                                        ${acc.status == 2 ? 'Đã khóa' : 'Khóa'}
                                    </a>
                                    <a href="javascript:void(0)" style="color: red;">Xóa</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Cục Toast Thông báo trượt từ phải sang -->
    <div id="toastNotification" class="toast">
        <span id="toastMessage">Đồng bộ thành công!</span>
    </div>

    <!-- Nhúng file JavaScript -->
    <script src="js/main.js"></script>

</body>
</html>