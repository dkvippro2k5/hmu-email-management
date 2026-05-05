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

        // 1. Lấy dữ liệu người dùng gõ trên web
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        String role = request.getParameter("role"); // Lấy role để phân biệt Admin và Student

        HttpSession session = request.getSession();

        // =================================================================
        // NHÁNH 1: XỬ LÝ CHO CÁN BỘ IT (ADMIN)
        // =================================================================
        if ("admin".equals(role)) {
            AdminDAO dao = new AdminDAO();
            ITAdmin admin = dao.checkLogin(user, pass);

            if (admin != null) {
                // Đúng: Cấp session và vào Dashboard quản lý
                session.setAttribute("currentAdmin", admin);
                response.sendRedirect("dashboard"); 
            } else {
                // Sai: Đẩy về trang login
                request.setAttribute("errorMessage", "Sai tài khoản hoặc mật khẩu Cán bộ IT!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } 
        // =================================================================
        // NHÁNH 2: XỬ LÝ CHO SINH VIÊN
        // =================================================================
        else if ("student".equals(role)) {
            StudentDAO dao = new StudentDAO();
            EmailAccount acc = dao.checkLogin(user, pass);

            if (acc != null) {
                session.setAttribute("user", acc);

                // KIỂM TRA FR-01.5: Đăng nhập lần đầu -> Bắt đổi mật khẩu
                if (acc.getStatus() == 0) {
                    response.sendRedirect("first-login.jsp"); 
                } 
                // KIỂM TRA TÀI KHOẢN KHÓA
                else if (acc.getStatus() == 2) {
                    request.setAttribute("errorMessage", "Tài khoản của bạn đang bị khóa/bảo lưu!");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                }
                // TRẠNG THÁI BÌNH THƯỜNG (Status = 1)
                else {
                    // Sinh viên bình thường thì cho vào trang riêng của SV
                    response.sendRedirect("student-portal.jsp"); 
                }
            } else { 
                request.setAttribute("errorMessage", "Sai Email hoặc mật khẩu Sinh viên!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } 
        // =================================================================
        // NHÁNH 3: PHÒNG HỜ LỖI CHƯA CHỌN QUYỀN
        // =================================================================
        else {
            request.setAttribute("errorMessage", "Vui lòng chọn Quyền đăng nhập!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}