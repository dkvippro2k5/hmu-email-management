package vn.edu.hmu.model;

import java.sql.Timestamp;

public class SupportRequest {
    private int requestId;
    private String studentId;
    private String subject;
    private String content;
    private int status; // 0: Pending, 1: Resolved
    private Timestamp createdAt;

    // Additional field for UI display (optional)
    private String studentName;

    public SupportRequest() {}

    public SupportRequest(String studentId, String subject, String content) {
        this.studentId = studentId;
        this.subject = subject;
        this.content = content;
    }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
}
