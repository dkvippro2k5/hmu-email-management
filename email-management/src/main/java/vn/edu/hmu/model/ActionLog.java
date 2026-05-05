package vn.edu.hmu.model;

import java.sql.Timestamp;

public class ActionLog {
    private int logId;
    private int adminId;
    private String targetEmail;
    private String actionType; // 'CREATE', 'SUSPEND', 'ACTIVATE', 'DELETE'
    private String reason;
    private Timestamp actionTime;

    // Constructor rỗng (Bắt buộc)
    public ActionLog() {}

    // Constructor dùng khi thêm mới log (Không cần truyền logId và actionTime vì MySQL tự sinh)
    public ActionLog(int adminId, String targetEmail, String actionType, String reason) {
        this.adminId = adminId;
        this.targetEmail = targetEmail;
        this.actionType = actionType;
        this.reason = reason;
    }

    // Constructor lấy dữ liệu từ DB lên
    public ActionLog(int logId, int adminId, String targetEmail, String actionType, String reason, Timestamp actionTime) {
        this.logId = logId;
        this.adminId = adminId;
        this.targetEmail = targetEmail;
        this.actionType = actionType;
        this.reason = reason;
        this.actionTime = actionTime;
    }

    // ==========================================
    // GETTER VÀ SETTER
    // ==========================================
    
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public String getTargetEmail() { return targetEmail; }
    public void setTargetEmail(String targetEmail) { this.targetEmail = targetEmail; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Timestamp getActionTime() { return actionTime; }
    public void setActionTime(Timestamp actionTime) { this.actionTime = actionTime; }
}