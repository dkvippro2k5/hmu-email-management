package vn.edu.hmu.model;

import java.util.Date;

public class Notification {
    private int id;
    private String studentId;
    private String title;
    private String message;
    private Date createdAt;
    private long createdAtTimestamp;
    private boolean isRead;

    public Notification() {}

    public Notification(String studentId, String title, String message) {
        this.studentId = studentId;
        this.title = title;
        this.message = message;
        this.isRead = false;
        this.createdAt = new Date();
        this.createdAtTimestamp = this.createdAt.getTime();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { 
        this.createdAt = createdAt; 
        this.createdAtTimestamp = createdAt != null ? createdAt.getTime() : 0;
    }
    public long getCreatedAtTimestamp() { return createdAtTimestamp; }
    public void setCreatedAtTimestamp(long createdAtTimestamp) { this.createdAtTimestamp = createdAtTimestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
