package vn.edu.hmu.controller;

import com.google.gson.Gson;
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.model.EmailAccount;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/search-accounts")
public class SearchAccountServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Cấu hình trả về JSON và hỗ trợ UTF-8
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Lấy từ khóa do người dùng nhập vào
        String keyword = request.getParameter("keyword");
        String statusFilter = request.getParameter("status"); // Có thể là "active", "inactive" hoặc null

        if (keyword == null) {
            keyword = ""; // Nếu không có từ khóa, tìm tất cả
        }

        int status = -1;
        if (statusFilter != null && !statusFilter.isEmpty()) {
            status = Integer.parseInt(statusFilter);
        }

        // Truy vấn database để lấy danh sách tài khoản phù hợp
        StudentDAO dao = new StudentDAO();
        List<EmailAccount> accounts = dao.searchAccounts(keyword, status);
        
        // Chuyển danh sách tài khoản thành JSON
        Gson gson = new Gson();
        String json = gson.toJson(accounts);

        // Trả về kết quả dưới dạng JSON
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}