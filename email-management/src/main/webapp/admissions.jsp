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
        body { font-family: 'Be Vietnam Pro', sans-serif; line-height: 1.6; color: var(--text); background-color: #f4f7f6; margin: 0; overflow-y: scroll; }
        
        .container { max-width: 1000px; margin: 30px auto; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        h1 { color: var(--primary); margin-bottom: 20px; }
        h2 { color: var(--accent); margin-top: 30px; margin-bottom: 10px; }
        p { margin-bottom: 15px; }
        footer { background: #343a40; color: #fff; padding: 20px; text-align: center; margin-top: 50px; }
    </style>
</head>
<body>
    <jsp:include page="components/header.jsp" />
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