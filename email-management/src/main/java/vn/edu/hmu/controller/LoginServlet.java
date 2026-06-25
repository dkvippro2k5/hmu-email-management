package vn.edu.hmu.controller;

import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.ITAdmin;
import vn.edu.hmu.dao.StudentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login") 
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Cài đặt tiếng Việt để nhỡ có báo lỗi thì không bị lỗi font
        request.setCharacterEncoding("UTF-8");

        // 1. Lấy dữ liệu người dùng gõ trên web (không còn lấy role nữa)
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        HttpSession session = request.getSession();

        // =================================================================
        // BƯỚC 1: KIỂM TRA XEM ĐÂY CÓ PHẢI LÀ CÁN BỘ IT (ADMIN) KHÔNG?
        // =================================================================
        AdminDAO adminDao = new AdminDAO();
        ITAdmin admin = adminDao.checkLogin(user, pass);

        if (admin != null) {
            // Đúng là Admin: Cấp session và vào Dashboard quản lý
            session.setAttribute("currentAdmin", admin);
            response.sendRedirect("dashboard"); 
            return; // Dừng hàm tại đây để không chạy xuống phần sinh viên nữa
        } 
        
        // =================================================================
        // BƯỚC 2: NẾU KHÔNG PHẢI ADMIN -> KIỂM TRA XEM CÓ PHẢI LÀ SINH VIÊN?
        // =================================================================
        StudentDAO studentDao = new StudentDAO();
        EmailAccount acc = studentDao.checkLogin(user, pass);

        if (acc != null) {
            session.setAttribute("user", acc);

            // KIỂM TRA ĐĂNG NHẬP LẦN ĐẦU (CHƯA ĐỔI MẬT KHẨU PORTAL HOẶC CHƯA KÍCH HOẠT)
            if (acc.getStatus() == 0 || studentDao.isPortalPasswordNull(acc.getStudentId())) {
                // Yêu cầu nhập mật khẩu mới và SĐT
                response.sendRedirect("first-login.jsp"); 
                return;
            } 
            
            // KIỂM TRA TÀI KHOẢN KHÓA
            if (acc.getStatus() == 2) {
                request.setAttribute("errorMessage", "Tài khoản của bạn đang bị khóa/bảo lưu. Vui lòng liên hệ IT!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            // TRẠNG THÁI BÌNH THƯỜNG (Status = 1)
            // Sinh viên vào thẳng Dashboard vì đã kích hoạt xong
            response.sendRedirect("student-portal.jsp");
            return; // Dừng hàm
        } 
        
        // =================================================================
        // BƯỚC 3: NẾU SAI CẢ 2 TRƯỜNG HỢP -> BÁO LỖI ĐĂNG NHẬP
        // =================================================================
        request.setAttribute("errorMessage", "Sai tên đăng nhập hoặc mật khẩu!");
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}