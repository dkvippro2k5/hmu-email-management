package vn.edu.hmu.model;

import java.sql.Timestamp;

public class Student {
    private String studentId;
    private String fullName;
    private String cccd;
    private String firstName;
    private String lastName;
    private String cohort;
    private String phoneNumber;
    private Timestamp createdAt;

    public Student() {}

    public Student(String studentId, String fullName, String cccd, String firstName,
                   String lastName, String cohort, String phoneNumber) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.cccd = cccd;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cohort = cohort;
        this.phoneNumber = phoneNumber;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCohort() { return cohort; }
    public void setCohort(String cohort) { this.cohort = cohort; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
