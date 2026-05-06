package vn.edu.hmu.controller;

// Xóa bỏ import Email cũ, thay bằng DAO và EmailAccount mới
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.EmailAccount;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpSession;


@WebServlet("/dashboard") // Đường dẫn ảo để truy cập
public class DashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("currentAdmin") == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang login
            response.sendRedirect("login.jsp");
            return;  
        }

        // 1. Gọi DAO để móc dữ liệu thật từ Database MySQL
        StudentDAO dao = new StudentDAO();
        List<EmailAccount> listAcc = dao.getAllAccounts();

        // 2. Gói danh sách này lại vào Request (Lưu ý: Tên biến là "dsTaiKhoan" để khớp với file JSP)
        request.setAttribute("dsTaiKhoan", listAcc);
        
        // 3. Chuyển hướng sang giao diện dashboard.jsp để in ra bảng
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}