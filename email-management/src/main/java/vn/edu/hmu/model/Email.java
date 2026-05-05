package vn.edu.hmu.model;

public class Email {
    private String sender;
    private String subject;
    private String time;
    private String status; // "Chưa đọc" hoặc "Đã đọc"

    // Hàm khởi tạo (Constructor) để tạo nhanh 1 bức email
    public Email(String sender, String subject, String time, String status) {
        this.sender = sender;
        this.subject = subject;
        this.time = time;
        this.status = status;
    }

    // Các hàm Getters để lấy dữ liệu ra hiển thị
    public String getSender() { return sender; }
    public String getSubject() { return subject; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
}