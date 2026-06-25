# Hệ thống Quản lý Cấp phát Email Sinh viên HMU

Đây là dự án quản lý và cấp phát tài khoản email cho sinh viên Trường Đại học Y Hà Nội (HMU). Hệ thống giúp tự động hóa quá trình nhập danh sách sinh viên, tạo email theo cú pháp chuẩn, cấp phát mật khẩu và theo dõi vòng đời của tài khoản (Hoạt động, Bảo lưu, Thu hồi).

## 🛠 Công nghệ sử dụng
- **Backend**: Java Servlet & JSP (Java 17)
- **Cơ sở dữ liệu**: MySQL 8.x
- **Build tool**: Maven
- **Thư viện chính**:
  - `Apache POI`: Hỗ trợ đọc và xử lý file Excel (danh sách sinh viên).
  - `jBCrypt`: Băm mật khẩu (Hashing) cho ứng dụng.
  - `Gson`: Xử lý dữ liệu JSON cho các API nội bộ.

## 🚀 Hướng dẫn cài đặt

### 1. Chuẩn bị Cơ sở dữ liệu
Hệ thống sử dụng MySQL. Bạn cần tạo Database có tên `email_management_db`.
*Lưu ý: Bạn có thể tham khảo cấu trúc bảng trong các file `.sql` đi kèm hoặc sử dụng bản backup schema của dự án.*

### 2. Cấu hình biến môi trường
Để bảo mật, dự án không chứa mật khẩu database trong mã nguồn. Bạn cần thiết lập biến môi trường sau trên máy tính của mình trước khi chạy:
- Tên biến: `DB_PASSWORD`
- Giá trị: `Mật khẩu MySQL của bạn` (vd: `root`, `123456`)

*Nếu bạn không muốn dùng biến môi trường, có thể sửa trực tiếp trong file `src/main/java/vn/edu/hmu/util/DBConnection.java` khi chạy ở máy local.*

### 3. Biên dịch và Khởi chạy
Sử dụng Maven để tải các thư viện phụ thuộc và đóng gói dự án:
```bash
# Xóa thư mục target cũ và đóng gói thành file .war
./mvnw clean package
```
Sau đó, deploy thư mục `target/email-management.war` (hoặc thư mục đã bung) vào Server (như Apache Tomcat 9/10).

## 🛡 Tính năng chính
- Nhập/Xuất danh sách sinh viên bằng file Excel (.xlsx).
- Giao diện Admin quản trị trạng thái tài khoản.
- Tự động mã hóa mật khẩu và cơ chế sinh tự động tên email.
- Cổng thông tin Sinh viên (Student Portal) để sinh viên đăng nhập lần đầu và nhận tài khoản, đổi mật khẩu.

## 📌 Lưu ý Bảo mật
- Tất cả mật khẩu của người dùng đều được mã hóa bằng thuật toán BCrypt trước khi lưu trữ.
- Tuyệt đối không commit các tệp `.env`, `cookies.txt` hay mật khẩu thật lên repository.
