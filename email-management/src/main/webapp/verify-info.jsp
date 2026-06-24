<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác nhận thông tin sinh viên - HMU</title>
    <!-- CSS -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- Inline CSS for styling similar to login -->
    <style>
        :root {
            --primary-color: #004d99; /* HMU blue */
            --primary-light: #e6f0fa;
            --text-color: #333;
            --bg-color: #f5f7fa;
            --white: #ffffff;
            --error-color: #e74c3c;
            --success-color: #2ecc71;
            --border-radius: 12px;
            --box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            --transition: all 0.3s ease;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--bg-color);
            background-image: linear-gradient(135deg, #f5f7fa 0%, #e6f0fa 100%);
            color: var(--text-color);
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            padding: 20px;
        }

        .verify-container {
            background-color: var(--white);
            border-radius: var(--border-radius);
            box-shadow: var(--box-shadow);
            width: 100%;
            max-width: 600px;
            padding: 40px;
            position: relative;
            overflow: hidden;
        }

        .verify-header {
            text-align: center;
            margin-bottom: 30px;
        }

        .verify-header h2 {
            color: var(--primary-color);
            font-size: 24px;
            font-weight: 700;
            margin: 0 0 10px;
        }

        .verify-header p {
            color: #666;
            font-size: 15px;
            margin: 0;
        }

        .info-group {
            margin-bottom: 20px;
        }

        .info-group label {
            display: block;
            font-size: 14px;
            font-weight: 600;
            color: #4a5568;
            margin-bottom: 8px;
        }

        .info-value {
            background-color: #f8fafc;
            border: 1px solid #e2e8f0;
            padding: 12px 16px;
            border-radius: 8px;
            color: #1a202c;
            font-size: 15px;
            font-weight: 500;
            display: flex;
            align-items: center;
        }

        .info-value i {
            color: var(--primary-color);
            margin-right: 12px;
            width: 20px;
            text-align: center;
        }

        .btn-confirm {
            width: 100%;
            padding: 14px;
            background-color: var(--primary-color);
            color: var(--white);
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition);
            margin-top: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .btn-confirm:hover {
            background-color: #003366;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 77, 153, 0.2);
        }

        .btn-confirm i {
            margin-right: 8px;
        }

        .alert-message {
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
            display: flex;
            align-items: center;
            background-color: rgba(231, 76, 60, 0.1);
            color: var(--error-color);
            border: 1px solid rgba(231, 76, 60, 0.2);
        }

        .alert-message i {
            margin-right: 8px;
        }
        
        .note {
            font-size: 13px;
            color: #718096;
            margin-top: 20px;
            text-align: center;
            line-height: 1.5;
        }
    </style>
</head>
<body>

<div class="verify-container">
    <div class="verify-header">
        <h2>Xác Nhận Thông Tin Sinh Viên</h2>
        <p>Vui lòng kiểm tra lại thông tin hồ sơ của bạn trước khi nhận tài khoản.</p>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert-message">
            <i class="fas fa-exclamation-circle"></i>
            ${errorMessage}
        </div>
    </c:if>

    <div class="info-group">
        <label>Mã Sinh Viên (MSSV)</label>
        <div class="info-value">
            <i class="fas fa-id-card"></i>
            ${student.studentId}
        </div>
    </div>

    <div class="info-group">
        <label>Họ và Tên</label>
        <div class="info-value">
            <i class="fas fa-user"></i>
            ${student.fullName}
        </div>
    </div>

    <div class="info-group">
        <label>Số CMND / CCCD</label>
        <div class="info-value">
            <i class="fas fa-address-card"></i>
            ${student.cccd}
        </div>
    </div>


    <div class="info-group">
        <label>Số Điện Thoại</label>
        <div class="info-value">
            <i class="fas fa-phone"></i>
            ${empty student.phoneNumber ? 'Chưa cập nhật' : student.phoneNumber}
        </div>
    </div>

    <form action="verify-info" method="post">
        <button type="submit" class="btn-confirm">
            <i class="fas fa-check-circle"></i> Xác nhận thông tin và Nhận tài khoản
        </button>
    </form>
    
    <div class="note">
        <i class="fas fa-info-circle"></i> Các trường thông tin trên là không thể chỉnh sửa. Nếu có sai sót, vui lòng liên hệ Phòng Quản lý Đào tạo Đại học sau khi nhận tài khoản.
    </div>
</div>

</body>
</html>
