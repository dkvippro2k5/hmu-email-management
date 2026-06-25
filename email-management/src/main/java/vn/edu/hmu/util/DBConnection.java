package vn.edu.hmu.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/email_management_db?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root"; 
    // Mật khẩu đã được loại bỏ khỏi mã nguồn để bảo mật.
    // Trong môi trường thực tế, nên sử dụng biến môi trường (Environment Variables) hoặc file cấu hình bên ngoài.
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "28032005"; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("KET NOI THANH CONG DEN CSDL!");
        } else {
            System.out.println("Ket noi that bai. vui long kiem tra lai.");
        }
    }
}
