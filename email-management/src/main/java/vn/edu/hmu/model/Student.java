package vn.edu.hmu.model;

import java.sql.Timestamp;

public class Student {
    private String studentId;
    private String fullName;
    private String className;
    private String department;
    private String cohort; // Niên khóa (K60, K61...)
    private String personalEmail;
    private Timestamp createdAt;

    // Constructor không tham số (Bắt buộc cho Java Beans)
    public Student() {}

    // Constructor đầy đủ tham số để khởi tạo nhanh
    public Student(String studentId, String fullName, String className, 
                   String department, String cohort, String personalEmail) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.className = className;
        this.department = department;
        this.cohort = cohort;
        this.personalEmail = personalEmail;
    }

    // Getter và Setter
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCohort() { return cohort; }
    public void setCohort(String cohort) { this.cohort = cohort; }

    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
