package vn.edu.hmu.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    
    // 1. DÁN USERNAME VÀ PASSWORD CỦA MAILTRAP VÀO ĐÂY
    private static final String MAILTRAP_USERNAME = "46314d439101f7"; 
    private static final String MAILTRAP_PASSWORD = "3d5f18bbab450d"; 

    public static void sendWelcomeEmail(String toEmail, String studentName, String hmuEmail, String defaultPassword) {
        
        // 2. CẤU HÌNH MÁY CHỦ SANG MAILTRAP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io"); // Host của Mailtrap
        props.put("mail.smtp.port", "2525"); // Port của Mailtrap

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAILTRAP_USERNAME, MAILTRAP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            // Email người gửi (Bạn gõ bừa gì cũng được vì Mailtrap sẽ gom hết lại)
            message.setFrom(new InternetAddress("admin@hmu.edu.vn", "Phòng IT - Đại học Y Hà Nội (HMU)"));
            
            // Email người nhận (Dù là mail fake thì nó vẫn bay vào Mailtrap)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Thông tin Tài khoản Email Sinh viên HMU");

            // Nội dung thư (Giữ nguyên như cũ)
            String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                    + "<h2 style='color: #0f4c75;'>Chào bạn " + studentName + ",</h2>"
                    + "<p>Phòng IT trường Đại học Y Hà Nội (HMU) đã khởi tạo thành công tài khoản email sinh viên của bạn.</p>"
                    + "<div style='background-color: #f0f2f5; padding: 15px; border-radius: 5px; margin: 20px 0;'>"
                    + "  <p><b>Tài khoản:</b> <span style='color: #d9534f;'>" + hmuEmail + "</span></p>"
                    + "  <p><b>Mật khẩu mặc định:</b> <span style='color: #d9534f;'>" + defaultPassword + "</span></p>"
                    + "</div>"
                    + "<p>Vui lòng truy cập <a href='http://localhost:8080/email-management/login.jsp' style='color: #3282b8; font-weight: bold;'>Cổng thông tin HMU</a> để đăng nhập và <b>BẮT BUỘC ĐỔI MẬT KHẨU</b> để kích hoạt tài khoản.</p>"
                    + "<p>Trân trọng,<br><b>Phòng IT HMU</b></p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Lệnh Gửi thư
            Transport.send(message);
            System.out.println("Đã gửi email thành công tới: " + toEmail + " (Đã bị bắt bởi Mailtrap)");

            } catch (Exception e) {
            System.out.println("Lỗi gửi mail: " + e.getMessage());
            }
            }

            public static void sendForgotPasswordEmail(String toEmail, String studentName, String newPassword) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
            props.put("mail.smtp.port", "2525");

            Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAILTRAP_USERNAME, MAILTRAP_PASSWORD);
            }
            });

            try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("admin@hmu.edu.vn", "Phòng IT - Đại học Y Hà Nội (HMU)"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Cấp lại mật khẩu tài khoản Email HMU");

            String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                    + "<h2 style='color: #0f4c75;'>Chào bạn " + studentName + ",</h2>"
                    + "<p>Hệ thống đã ghi nhận yêu cầu cấp lại mật khẩu cho tài khoản của bạn.</p>"
                    + "<div style='background-color: #f0f2f5; padding: 15px; border-radius: 5px; margin: 20px 0;'>"
                    + "  <p><b>Mật khẩu mới của bạn là:</b> <span style='color: #d9534f; font-size: 18px; font-weight: bold;'>" + newPassword + "</span></p>"
                    + "</div>"
                    + "<p>Vui lòng đăng nhập và đổi lại mật khẩu ngay lập tức để đảm bảo an toàn.</p>"
                    + "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng liên hệ ngay với Phòng IT.</p>"
                    + "<p>Trân trọng,<br><b>Phòng IT HMU</b></p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
            } catch (Exception e) {
            e.printStackTrace();
            }
            }
            
            // Giả lập đồng bộ Khóa/Bảo lưu tài khoản lên Dịch vụ Email đám mây (Google Workspace/O365)
            public static boolean syncSuspendWithCloud(String emailAddress) throws Exception {
                // Đã bỏ delay mạng giả lập để tăng tốc độ xử lý hàng loạt
                
                // Giả lập lỗi API cho các email bắt đầu bằng từ "error" để test chức năng lỗi thủ công
                if (emailAddress != null && emailAddress.toLowerCase().startsWith("error")) {
                    throw new Exception("API Timeout: Dịch vụ Cloud không phản hồi đối với email " + emailAddress);
                }
                
                // Giả lập thành công
                System.out.println("Đồng bộ khóa tài khoản lên Cloud thành công cho email: " + emailAddress);
                return true;
            }

            public static String sendRevokeWarningEmail(String toEmail, String studentName) {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
                props.put("mail.smtp.port", "2525");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(MAILTRAP_USERNAME, MAILTRAP_PASSWORD);
                    }
                });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("admin@hmu.edu.vn", "Phòng IT - Đại học Y Hà Nội (HMU)"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                    message.setSubject("CẢNH BÁO: Thu hồi tài khoản Email Sinh viên");

                    String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                            + "<h2 style='color: #d9534f;'>Chào bạn " + studentName + ",</h2>"
                            + "<p>Phòng IT trường Đại học Y Hà Nội (HMU) thông báo: Tài khoản email sinh viên của bạn đã được đưa vào danh sách chờ thu hồi/xóa vĩnh viễn.</p>"
                            + "<div style='background-color: #fcf8e3; padding: 15px; border-left: 4px solid #f0ad4e; margin: 20px 0;'>"
                            + "  <p><b>Thời gian ân hạn:</b> 30 ngày kể từ hôm nay.</p>"
                            + "  <p>Vui lòng sao lưu toàn bộ dữ liệu quan trọng trên Google Drive và Gmail của bạn trước thời hạn này. Sau 30 ngày, hệ thống sẽ tự động xóa tài khoản mà không báo trước thêm.</p>"
                            + "</div>"
                            + "<p>Nếu bạn cho rằng đây là một sự nhầm lẫn (bạn chưa tốt nghiệp hoặc không thuộc diện thu hồi), vui lòng truy cập <a href='http://localhost:8080/email-management/' style='color: #3282b8; font-weight: bold;'>Cổng thông tin HMU</a> để gửi yêu cầu hỗ trợ (Ticket).</p>"
                            + "<p>Trân trọng,<br><b>Phòng IT HMU</b></p>"
                            + "</div>";

                    message.setContent(htmlContent, "text/html; charset=utf-8");
                    Transport.send(message);
                    System.out.println("Đã gửi email cảnh báo thu hồi tới: " + toEmail);
                    return htmlContent;
                } catch (Exception e) {
                    System.out.println("Lỗi gửi mail cảnh báo: " + e.getMessage());
                    return null;
                }
            }
}