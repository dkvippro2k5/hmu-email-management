package vn.edu.hmu.model;

import java.sql.Date;

public class EmailAccount {
    private String emailAddress;
    private String studentId;
    private String passwordHash;
    private int status; // 0: Chờ, 1: Hoạt động, 2: Bảo lưu, 3: Chờ xóa
    private Date activationDate;
    private String studentName; // Thêm trường này để hiển thị tên sinh viên trên Dashboard
    private String decisionNumber; // Thêm trường này để lưu số quyết định khi khóa tài khoản

    public EmailAccount() {}

    // HÀM MỚI BỔ SUNG: Dành cho chức năng Import Excel (5 tham số)
    public EmailAccount(String emailAddress, String studentId, String passwordHash, 
                        int status, Date activationDate) {
        this.emailAddress = emailAddress;
        this.studentId = studentId;
        this.passwordHash = passwordHash;
        this.status = status;
        this.activationDate = activationDate;
    }

    public EmailAccount(String emailAddress, String studentId, String passwordHash, 
                        int status, Date activationDate, String decisionNumber) {
        this.emailAddress = emailAddress;
        this.studentId = studentId;
        this.passwordHash = passwordHash;
        this.status = status;
        this.activationDate = activationDate;
        this.decisionNumber = decisionNumber;
    }

    // Getter và Setter
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    // Thêm 2 hàm này vào để JSP đọc được
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public Date getActivationDate() { return activationDate; }
    public void setActivationDate(Date activationDate) { this.activationDate = activationDate; }

    public String getDecisionNumber() { return decisionNumber; }
    public void setDecisionNumber(String decisionNumber) { this.decisionNumber = decisionNumber; }
}
