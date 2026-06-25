package vn.edu.hmu.controller;

// Xoa bo import Email cu, thay bang DAO va EmailAccount moi
import vn.edu.hmu.dao.StudentDAO;
import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.model.EmailAccount;
import vn.edu.hmu.model.ActionLog;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpSession;


@WebServlet("/dashboard") // Duong dan ao de truy cap
public class DashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("currentAdmin") == null) {
            // Neu chua dang nhap, chuyen huong ve trang login
            response.sendRedirect("login.jsp");
            return;  
        }

        // 1. Goi DAO de moc du lieu that tu Database MySQL
        StudentDAO studentDAO = new StudentDAO();
        AdminDAO adminDAO = new AdminDAO();

        // Lấy danh sách tài khoản cho bảng danh sách
        List<EmailAccount> listAcc = studentDAO.getAllAccounts();
        
        // Lấy danh sách tài khoản đang bảo lưu
        List<EmailAccount> suspendedAccList = studentDAO.getSuspendedAccountsList();

        // Lấy danh sách tài khoản chờ thu hồi
        List<EmailAccount> revokedAccList = studentDAO.getPendingRevokeAccountsList();

        // Lay thong ke
        int totalAccounts = studentDAO.getTotalAccounts();
        int activeAccounts = studentDAO.getActiveAccounts();
        int suspendedAccounts = studentDAO.getSuspendedAccounts();
        int pendingRevokeAccounts = studentDAO.getPendingRevokeAccounts();

        // Lay logs gan day (20 logs) cho dashboard
        List<ActionLog> recentLogs = adminDAO.getRecentLogs(20);
        // Lấy danh sách log nhiều hơn (ví dụ 100) cho tab Nhật ký hoạt động
        List<ActionLog> allLogs = adminDAO.getRecentLogs(100);

        // 2. Goi du lieu vao request attributes
        request.setAttribute("dsTaiKhoan", listAcc);
        request.setAttribute("dsBaoLuu", suspendedAccList);
        request.setAttribute("dsThuHoi", revokedAccList);
        request.setAttribute("totalAccounts", totalAccounts);
        request.setAttribute("activeAccounts", activeAccounts);
        request.setAttribute("suspendedAccounts", suspendedAccounts);
        request.setAttribute("pendingRevokeAccounts", pendingRevokeAccounts);
        request.setAttribute("recentLogs", recentLogs);
        request.setAttribute("allLogs", allLogs);
        
        // 3. Chuyen huong sang giao dien dashboard.jsp de in ra bang
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}