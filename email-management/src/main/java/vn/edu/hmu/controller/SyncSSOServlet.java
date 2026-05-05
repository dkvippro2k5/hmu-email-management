package vn.edu.hmu.controller;

import vn.edu.hmu.dao.StudentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/sync-sso")
public class SyncSSOServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Cấu hình trả về kiểu JSON cho API
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        if (session.getAttribute("currentAdmin") == null) {
            out.print("{\"status\":\"error\", \"message\":\"Không có quyền truy cập!\"}");
            out.flush();
            return;
        }

        StudentDAO dao = new StudentDAO();
        int totalAccounts = dao.getAllAccounts().size();

        // Giả lập độ trễ mạng khi gọi API sang E-learning (2 giây)
        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Trả về JSON thông báo thành công
        String jsonResponse = "{\"status\":\"success\", \"message\":\"Đã đồng bộ thành công " + totalAccounts + " tài khoản!\"}";
        out.print(jsonResponse);
        out.flush();
    }
}