package vn.edu.hmu.model;

import java.sql.Date;

public class EmailAccount {
    private String emailAddress;
    private String studentId;
    private String passwordHash;
    private int status; // 0: Chờ, 1: Hoạt động, 2: Bảo lưu, 3: Chờ xóa
    private Date activationDate;
    
    // Các biến phụ để hiển thị thông tin kèm theo trên bảng
    private String studentName; 
    private String cccd;
    private String firstName;
    private String lastName;
    private String personalEmail;
    private String cohort;
    private String phoneNumber;
    
    private String decisionNumber; // Lưu số quyết định bảo lưu
    private Date scheduledDeleteDate;

    public EmailAccount() {}

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

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

    public String getCohort() { return cohort; }
    public void setCohort(String cohort) { this.cohort = cohort; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public Date getActivationDate() { return activationDate; }
    public void setActivationDate(Date activationDate) { this.activationDate = activationDate; }

    public String getDecisionNumber() { return decisionNumber; }
    public void setDecisionNumber(String decisionNumber) { this.decisionNumber = decisionNumber; }

    public Date getScheduledDeleteDate() { return scheduledDeleteDate; }
    public void setScheduledDeleteDate(Date scheduledDeleteDate) { this.scheduledDeleteDate = scheduledDeleteDate; }
}
