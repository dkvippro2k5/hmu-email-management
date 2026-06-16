package vn.edu.hmu.model;

import java.sql.Date;

public class EmailAccount {
    private String emailAddress;
    private String studentId;
    private String passwordHash;
    private int status; // 0: Chờ, 1: Hoạt động, 2: Bảo lưu, 3: Chờ xóa
    private Date activationDate;
    private String studentName; // Thêm trường này để hiển thị tên sinh viên trên Dashboard
    private String gender;
    private String dateOfBirth;
    private String className;
    private String major;
    private String department;
    private String cohort;
    private String personalEmail;
    private String decisionNumber; // Thêm trường này để lưu số quyết định khi khóa tài khoản
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

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCohort() { return cohort; }
    public void setCohort(String cohort) { this.cohort = cohort; }

    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

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
