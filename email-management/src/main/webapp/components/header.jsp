<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="vn.edu.hmu.model.EmailAccount" %>
<%@ page import="vn.edu.hmu.model.ITAdmin" %>

<style>
    /* Ensure all required variables exist */
    :root {
        --primary: #0056b3;
        --secondary: #6c757d;
        --bg: #f4f7f6;
        --surface: #ffffff;
        --border: #e5e7eb;
        --accent: #3b82f6;
        --accent2: #2563eb;
        --text: #111827;
        --text2: #4b5563;
        --text3: #6b7280;
    }

    /* Reset and Typography */
    body {
        font-family: 'Be Vietnam Pro', 'Arial', sans-serif;
        background: var(--bg);
        color: var(--text);
        line-height: 1.6;
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        overflow-y: scroll; /* Fix taskbar shifting */
        margin: 0;
        padding: 0;
    }
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

    /* Standard Header */
    header { background: #fff; padding: 20px 5% !important; margin: 0 !important; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 5px rgba(0,0,0,0.1); border: none !important; }
    .logo-section h1 { font-size: 24px; color: var(--primary); font-weight: 800; margin: 0 !important; padding: 0 !important; text-align: left !important; line-height: 1 !important; }
    .login-btn { padding: 10px 20px; background: var(--accent); color: #fff; text-decoration: none; border-radius: 8px; font-weight: 600; transition: 0.3s; display: inline-block; margin: 0 !important;}
    .login-btn:hover { background: #2563eb; }
    .user-info { font-size: 14px; color: var(--secondary); margin-right: 15px; }

    /* Standard Navigation Taskbar */
    nav { background: var(--primary); color: #fff; padding: 0 5% !important; margin: 0 !important; }
    nav ul { list-style: none; display: flex; margin: 0 !important; padding: 0 !important; }
    nav ul li a { display: block; padding: 15px 20px !important; margin: 0 !important; color: #fff; text-decoration: none; font-weight: 500; transition: 0.3s; line-height: 1.5 !important; }
    nav ul li a:hover { background: rgba(255,255,255,0.1); }

    /* Sticky Footer globally */
    footer { margin-top: auto !important; width: 100%; }

    /* Sidebar Styles for Notifications */
    .sidebar-overlay {
        position: fixed;
        top: 0; left: 0; right: 0; bottom: 0;
        background: rgba(0,0,0,0.4);
        display: none;
        z-index: 1000;
    }
    .sidebar-overlay.open { display: block; }
    .sidebar-box {
        position: absolute;
        top: 0; right: -400px;
        width: 400px;
        max-width: 100%;
        height: 100%;
        background: #fff;
        box-shadow: -5px 0 25px rgba(0,0,0,0.1);
        transition: right 0.3s ease;
        display: flex;
        flex-direction: column;
    }
    .sidebar-overlay.open .sidebar-box { right: 0; }
    .sidebar-header {
        padding: 20px;
        border-bottom: 1px solid var(--border);
        display: flex;
        justify-content: space-between;
        align-items: center;
        background: #f8fafc;
    }
    .sidebar-header h3 { font-size: 18px; margin: 0; color: var(--text); }
    .close-btn { cursor: pointer; font-size: 20px; color: var(--secondary); background: none; border: none; padding: 5px; }
    .close-btn:hover { color: var(--text); }
    .icon-btn { cursor: pointer; font-size: 18px; color: var(--accent); background: none; border: none; padding: 5px; display: flex; align-items: center; }
    .icon-btn:hover { color: var(--accent2); }
    .sidebar-body {
        padding: 20px;
        flex: 1;
        overflow-y: auto;
    }
    .notif-item {
        padding: 15px;
        border-bottom: 1px solid var(--border);
        cursor: pointer;
        transition: background 0.2s;
    }
    .notif-item:hover { background: #f1f5f9; }
    .notif-item.unread { background: #eff6ff; }
    .notif-item.unread strong { color: var(--accent); }
</style>

<header>
    <div class="logo-section">
        <h1>Trường Đại học Y Hà Nội</h1>
    </div>
    <div style="display: flex; align-items: center;">
        <% 
            EmailAccount user = (EmailAccount) session.getAttribute("user");
            ITAdmin admin = (ITAdmin) session.getAttribute("currentAdmin");
            if (user != null || admin != null) {
                String name = (user != null) ? user.getStudentId() : admin.getFullName();
                String dashboardLink = (admin != null) ? "dashboard" : "student-portal.jsp";
        %>
            <% if (user != null) { %>
            <div style="position: relative; margin-right: 20px; cursor: pointer;" onclick="openNotificationModal()">
                <span style="font-size: 24px;">🔔</span>
                <span id="unreadBadge" style="position: absolute; top: -5px; right: -10px; background: #ef4444; color: white; border-radius: 50%; padding: 2px 6px; font-size: 10px; font-weight: bold; display: none;">0</span>
            </div>
            <% } %>

            <span class="user-info">Xin chào, <strong><%= name %></strong></span>
            <% if (admin != null) { %>
                <a href="<%= dashboardLink %>" class="login-btn">Vào hệ thống</a>
            <% } %>
            <a href="logout" class="login-btn" style="background-color: #ef4444; margin-left: 10px;">Đăng xuất</a>
        <% } else { %>
            <a href="login.jsp" class="login-btn">Đăng nhập</a>
        <% } %>
    </div>
</header>

<nav>
    <ul>
        <li><a href="index.jsp">Trang chủ</a></li>
        <li><a href="admissions.jsp">Tuyển sinh - Đào tạo</a></li>
        <li><a href="it-services">Hệ thống và dịch vụ CNTT</a></li>
        <li><a href="support">Liên hệ hỗ trợ</a></li>
        <% if (user != null) { %>
            <li><a href="personal-info">Thông tin cá nhân</a></li>
            <li><a href="student-portal.jsp">Tài khoản Email</a></li>
        <% } %>
    </ul>
</nav>

<!-- Notification Sidebar -->
<div class="sidebar-overlay" id="notifSidebar" onclick="if(event.target===this) closeNotificationSidebar()">
    <div class="sidebar-box">
        <div class="sidebar-header">
            <div style="display: flex; align-items: center; gap: 10px;">
                <button id="btnBackNotif" class="icon-btn" style="display:none;" onclick="showNotifList()">⬅</button>
                <h3>📢 Thông báo</h3>
            </div>
            <button class="close-btn" onclick="closeNotificationSidebar()">✕</button>
        </div>
        
        <!-- List View -->
        <div class="sidebar-body" id="notifListView">
            <div id="notificationList" style="display: flex; flex-direction: column;">
                <div style="text-align: center; color: var(--text3); padding: 20px;">Đang tải thông báo...</div>
            </div>
        </div>

        <!-- Detail View -->
        <div class="sidebar-body" id="notifDetailView" style="display:none; background: #fff;">
            <h3 id="detailTitle" style="margin-bottom: 10px; color: var(--text); font-size: 18px;"></h3>
            <div id="detailDate" style="font-size: 13px; color: var(--text3); margin-bottom: 25px; padding-bottom: 15px; border-bottom: 1px solid var(--border);"></div>
            <div id="detailMessage" style="font-size: 15px; color: var(--text2); line-height: 1.6; white-space: pre-wrap;"></div>
        </div>
    </div>
</div>

<script>
    let allNotifications = [];

    document.addEventListener('DOMContentLoaded', function() {
        const badge = document.getElementById('unreadBadge');
        if(badge) fetchNotifications();
    });

    function openNotificationModal() {
        document.getElementById('notifSidebar').classList.add('open');
        showNotifList();
        fetchNotifications(); // Refresh when opening
    }

    function closeNotificationSidebar() {
        document.getElementById('notifSidebar').classList.remove('open');
    }

    function showNotifList() {
        document.getElementById('notifListView').style.display = 'block';
        document.getElementById('notifDetailView').style.display = 'none';
        document.getElementById('btnBackNotif').style.display = 'none';
    }

    function showNotifDetail(id) {
        const notif = allNotifications.find(n => n.id === id);
        if (!notif) return;

        document.getElementById('notifListView').style.display = 'none';
        document.getElementById('notifDetailView').style.display = 'block';
        document.getElementById('btnBackNotif').style.display = 'flex';

        const dateObj = new Date(notif.createdAtTimestamp || notif.createdAt);
        const dateStr = dateObj.toLocaleDateString('vi-VN') + ' ' + dateObj.toLocaleTimeString('vi-VN');

        document.getElementById('detailTitle').textContent = notif.title;
        document.getElementById('detailDate').textContent = dateStr;
        document.getElementById('detailMessage').textContent = notif.message;

        if (!notif.read) {
            markAsRead(id);
        }
    }

    function fetchNotifications() {
        fetch('api/notifications')
            .then(response => {
                if (response.ok) return response.json();
                throw new Error('Failed to fetch');
            })
            .then(data => {
                allNotifications = data.notifications || [];
                const list = document.getElementById('notificationList');
                const badge = document.getElementById('unreadBadge');
                
                if (data.unreadCount > 0) {
                    badge.textContent = data.unreadCount > 99 ? '99+' : data.unreadCount;
                    badge.style.display = 'inline-block';
                } else {
                    badge.style.display = 'none';
                }

                list.innerHTML = '';
                if (allNotifications.length > 0) {
                    allNotifications.forEach(n => {
                        const dateObj = new Date(n.createdAtTimestamp || n.createdAt);
                        const dateStr = dateObj.toLocaleDateString('vi-VN') + ' ' + dateObj.toLocaleTimeString('vi-VN');
                        
                        const div = document.createElement('div');
                        div.className = 'notif-item ' + (n.read ? '' : 'unread');
                        div.onclick = () => showNotifDetail(n.id);

                        const previewMsg = n.message.length > 60 ? n.message.substring(0, 60) + '...' : n.message;

                        div.innerHTML = `
                            <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
                                <strong style="font-size: 14px;">\${n.title}</strong>
                            </div>
                            <div style="font-size: 13px; color: var(--text2); margin-bottom: 6px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">\${previewMsg}</div>
                            <div style="font-size: 11px; color: var(--text3);">\${dateStr}</div>
                        `;
                        list.appendChild(div);
                    });
                } else {
                    list.innerHTML = '<div style="text-align: center; color: var(--text3); padding: 20px;">Không có thông báo nào.</div>';
                }
            })
            .catch(error => {
                console.error('Error fetching notifications:', error);
                const list = document.getElementById('notificationList');
                if(list) list.innerHTML = '<div style="text-align: center; color: #ef4444; padding: 20px;">Lỗi khi tải thông báo.</div>';
            });
    }

    function markAsRead(id) {
        const formData = new FormData();
        formData.append('action', 'markRead');
        formData.append('id', id);

        fetch('api/notifications', {
            method: 'POST',
            body: new URLSearchParams(formData)
        }).then(response => {
            if (response.ok) {
                const notif = allNotifications.find(n => n.id === id);
                if(notif) notif.read = true;
                fetchNotifications();
            }
        });
    }
</script>
