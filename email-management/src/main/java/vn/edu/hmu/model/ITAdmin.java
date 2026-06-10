package vn.edu.hmu.model;

public class ITAdmin {
    private String adminID;
    private String username;
    private String fullName;
    private String role;

    public ITAdmin() {}

    public ITAdmin(String adminID, String username, String fullName, String role) {
        this.adminID = adminID;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAdminID() { return adminID; }
    public void setAdminID(String adminID) { this.adminID = adminID; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}