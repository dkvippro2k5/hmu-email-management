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
}