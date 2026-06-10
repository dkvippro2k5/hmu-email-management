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

        // Lấy từ khóa và các bộ lọc
        String keyword = request.getParameter("keyword");
        String statusFilter = request.getParameter("status");
        String className = request.getParameter("className");
        String department = request.getParameter("department");
        String major = request.getParameter("major");
        String cohort = request.getParameter("cohort");

        if (keyword == null) keyword = "";

        int status = -1;
        try {
            if (statusFilter != null && !statusFilter.isEmpty()) {
                status = Integer.parseInt(statusFilter);
            }
        } catch (NumberFormatException ignored) {}

        // Truy vấn database với đầy đủ bộ lọc
        StudentDAO dao = new StudentDAO();
        List<EmailAccount> accounts = dao.searchAccountsAdvanced(keyword, status, className, department, major, cohort);
        
        // Chuyển danh sách tài khoản thành JSON
        Gson gson = new Gson();
        String json = gson.toJson(accounts);

        // Trả về kết quả dưới dạng JSON
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}