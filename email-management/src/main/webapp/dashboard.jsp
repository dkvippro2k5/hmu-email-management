<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Email Management - HMU</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
    
    <style>
        /* ================= BIẾN CSS (SÁNG & HIỆN ĐẠI) ================= */
        :root {
            --bg: #f0f2f5;             /* Nền tổng thể bên ngoài (Xám cực nhạt) */
            --bg2: #ffffff;            /* Nền Sidebar & Topbar (Trắng) */
            --bg3: #f9fafb;            /* Nền ô tìm kiếm, header bảng */
            --surface: #ffffff;        /* Nền các khối card, bảng (Trắng) */
            --surface2: #f3f4f6;       /* Nền modal, màu nền khi hover */
            --border: #e5e7eb;         /* Viền xám nhạt */
            --border2: #d1d5db;        /* Viền xám đậm hơn chút */
            
            --accent: #3b82f6;         /* Xanh lam chủ đạo */
            --accent2: #2563eb;        /* Xanh lam nhấn */
            --accent-glow: rgba(59,130,246,0.25);
            
            --red: #ef4444; 
            --green: #10b981; 
            --yellow: #f59e0b;
            
            --text-active: #ffffff;    /* Chữ trắng cho item đang chọn */
            --bg-active: #111827;      /* Nền đen đậm cho item đang chọn */
            
            --text: #111827;           /* Chữ chính (Đen đậm) */
            --text2: #4b5563;          /* Chữ phụ (Xám đậm) */
            --text3: #6b7280;          /* Chữ chú thích, mờ */
            
            --sidebar-w: 260px; 
            --header-h: 64px; 
            --radius: 12px;
        }

        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        
        body {
            font-family: 'Arial', sans-serif;
            background: var(--bg); color: var(--text);
            display: flex; height: 100vh; overflow: hidden;
            font-size: 14px;
        }

        /* ================= SIDEBAR (TASKBAR) ================= */
.sidebar {
    width: var(--sidebar-w);
    background: var(--bg2);
    border-right: 1px solid var(--border);
    display: flex;
    flex-direction: column;
    flex-shrink: 0;
    z-index: 10;
}

.sidebar-header {
    padding: 20px;
    font-size: 18px;
    font-weight: 800;
    color: var(--text);
    border-bottom: 1px solid var(--border);
    display: flex;
    align-items: center;
    gap: 10px;
}

.sidebar-nav {
    flex: 1;
    padding: 12px 14px;
    overflow-y: auto;
}

.nav-section-label {
    font-size: 10.5px;
    font-weight: 700;
    color: var(--text3);
    text-transform: uppercase;
    letter-spacing: 0.08em;
    padding: 16px 10px 6px;
    margin-top: 4px;
}

/* Nav Items cơ bản */
.nav-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 14px;
    margin-bottom: 4px;
    color: var(--text2);
    text-decoration: none;
    font-weight: 600;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    position: relative;
    font-size: 13.5px;
}

/* Hiệu ứng Hover */
.nav-item:hover:not(.active) {
    background: var(--surface2);
    color: var(--text);
}

/* Style cho Module Đang chọn (Màu đen) */
.nav-item.active {
    background: var(--bg-active);
    color: var(--text-active);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Vạch nhấn bên trái cho thẻ active */
.nav-item.active::before {
    content: '';
    position: absolute;
    left: 0;
    top: 15%;
    bottom: 15%;
    width: 4px;
    background: var(--accent);
    border-radius: 0 4px 4px 0;
}

.nav-icon {
    font-size: 16px;
    width: 20px;
    text-align: center;
}

.logout-btn {
    padding: 16px 24px;
    border-top: 1px solid var(--border);
    color: var(--red);
    text-decoration: none;
    font-weight: 600;
    transition: 0.2s;
    display: flex;
    align-items: center;
    gap: 10px;
    background: transparent;
}

.logout-btn:hover {
    background: #fef2f2;
}

/* ================= MAIN CONTENT & TOPBAR ================= */
.main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.topbar {
    height: var(--header-h);
    background: var(--bg2);
    border-bottom: 1px solid var(--border);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    flex-shrink: 0;
}

.page-title {
    font-size: 17px;
    font-weight: 700;
    color: var(--text);
}

.page-title span {
    color: var(--text3);
    font-weight: 500;
    font-size: 13px;
    margin-left: 8px;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 13px;
    color: var(--text2);
}

.user-info b {
    color: var(--text);
}

.avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--accent), #7b5fff);
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    color: #fff;
}

/* ================= CÁC TRANG MODULE ================= */
.page {
    display: none;
    flex: 1;
    flex-direction: column;
    overflow: hidden;
    padding: 24px;
}

.page.active {
    display: flex;
    animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(10px);
    }
    to {
        opacity: 1;
        transform: none;
    }
}

/* ================= ACTION BAR ================= */
.action-bar {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: var(--radius) var(--radius) 0 0;
    padding: 16px 20px;
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    align-items: center;
    justify-content: space-between;
    flex-shrink: 0;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
}

.search-box {
    background: var(--bg3);
    border: 1px solid var(--border);
    color: var(--text);
    border-radius: 8px;
    padding: 9px 12px;
    font-size: 13px;
    font-family: inherit;
    outline: none;
    transition: 0.2s;
}

.search-box:focus {
    border-color: var(--accent);
    background: var(--surface);
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.btn {
    display: inline-flex;
    align-items: center;
    gap: 7px;
    padding: 9px 16px;
    border-radius: 8px;
    font-size: 13px;
    font-weight: 600;
    font-family: inherit;
    cursor: pointer;
    border: none;
    transition: all 0.2s;
    text-decoration: none;
}

.btn-primary {
    background: var(--accent);
    color: #fff;
}

.btn-primary:hover {
    background: var(--accent2);
    box-shadow: 0 4px 12px var(--accent-glow);
    transform: translateY(-1px);
}

.btn-outline {
    background: transparent;
    color: var(--text);
    border: 1px solid var(--border2);
}

.btn-outline:hover {
    background: var(--surface2);
    border-color: var(--text3);
}

.btn-danger {
    background: rgba(239, 68, 68, 0.1);
    color: var(--red);
    border: 1px solid rgba(239, 68, 68, 0.2);
}

.btn-danger:hover {
    background: rgba(239, 68, 68, 0.15);
}

.btn-success {
    background: rgba(16, 185, 129, 0.1);
    color: var(--green);
    border: 1px solid rgba(16, 185, 129, 0.2);
}

.btn-warning {
    background: rgba(245, 158, 11, 0.1);
    color: var(--yellow);
    border: 1px solid rgba(245, 158, 11, 0.2);
}

.btn-sm {
    padding: 6px 10px;
    font-size: 12px;
}

/* ================= TABLE & BỐ CỤC KHÔNG CUỘN ================= */
.table-wrap {
    background: var(--surface);
    border: 1px solid var(--border);
    border-top: none;
    border-radius: 0 0 var(--radius) var(--radius);
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
}

.table-scroll {
    flex: 1;
    overflow-y: auto;
}

table {
    width: 100%;
    border-collapse: collapse;
}

th {
    padding: 14px 16px;
    text-align: left;
    font-size: 11px;
    font-weight: 700;
    color: var(--text3);
    text-transform: uppercase;
    border-bottom: 1px solid var(--border);
    background: var(--bg3);
    position: sticky;
    top: 0;
    z-index: 5;
}

td {
    padding: 14px 16px;
    border-bottom: 1px solid var(--border);
    font-size: 13px;
    color: var(--text2);
}

tr:hover td {
    background: var(--surface2);
}

.td-main {
    color: var(--text);
    font-weight: 600;
}

.mono {
    font-family: 'JetBrains Mono', monospace;
    font-size: 12.5px;
}

/* Phân trang */
.pagination {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 20px;
    border-top: 1px solid var(--border);
    background: var(--surface);
    flex-shrink: 0;
}

.page-info {
    font-size: 12px;
    color: var(--text3);
    font-weight: 500;
}

.page-btns {
    display: flex;
    gap: 4px;
}

.page-btn {
    width: 32px;
    height: 32px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid var(--border);
    background: var(--surface);
    color: var(--text2);
    cursor: pointer;
    font-size: 12px;
    transition: all 0.15s;
    font-weight: 600;
}

.page-btn:hover:not(.active) {
    background: var(--surface2);
    border-color: var(--border2);
}

.page-btn.active {
    background: var(--bg-active);
    border-color: var(--bg-active);
    color: #fff;
}

/* ================= BADGES ================= */
.badge {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    padding: 4px 10px;
    border-radius: 20px;
    font-size: 11px;
    font-weight: 600;
    border: 1px solid transparent;
}

.badge-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: currentColor;
}

.stt-active {
    background: rgba(16, 185, 129, 0.1);
    color: #059669;
    border-color: rgba(16, 185, 129, 0.2);
}

.stt-pending {
    background: rgba(245, 158, 11, 0.1);
    color: #d97706;
    border-color: rgba(245, 158, 11, 0.2);
}

.stt-suspended {
    background: rgba(107, 114, 128, 0.1);
    color: var(--text3);
    border-color: rgba(107, 114, 128, 0.2);
}

.stt-revoking {
    background: rgba(239, 68, 68, 0.1);
    color: #dc2626;
    border-color: rgba(239, 68, 68, 0.2);
}

/* ================= FILTER PANEL ================= */
.filter-container {
    position: relative;
    display: inline-block;
}

.filter-panel {
    position: absolute;
    top: calc(100% + 10px);
    left: 0;
    width: 320px;
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: var(--radius);
    box-shadow: 0 10px 25px rgba(0,0,0,0.1);
    z-index: 50;
    padding: 20px;
    display: none;
    flex-direction: column;
    gap: 14px;
    animation: slideDown 0.2s ease;
}

.filter-panel.open {
    display: flex;
}

@keyframes slideDown {
    from { opacity: 0; transform: translateY(-10px); }
    to { opacity: 1; transform: translateY(0); }
}

.filter-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 4px;
}

.filter-title {
    font-weight: 700;
    font-size: 13px;
    color: var(--text);
}

.filter-badge {
    background: var(--accent);
    color: #fff;
    font-size: 10px;
    padding: 2px 6px;
    border-radius: 10px;
    margin-left: 4px;
    display: none; /* Hiện khi count > 0 */
}

.filter-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 6px;
    padding-top: 14px;
    border-top: 1px solid var(--border);
}

/* ================= MODAL CHỈNH SỬA ================= */
.modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(15, 23, 42, 0.5);
    backdrop-filter: blur(2px);
    z-index: 100;
    display: none;
    align-items: center;
    justify-content: center;
    padding: 20px;
}

.modal-overlay.open {
    display: flex;
    animation: fadeIn 0.2s ease;
}

.modal {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 16px;
    width: 100%;
    max-width: 480px;
    box-shadow: 0 24px 40px rgba(0, 0, 0, 0.15);
    animation: slideUp 0.25s ease;
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: none;
    }
}

.modal-header {
    display: flex;
    justify-content: space-between;
    padding: 20px 24px;
    border-bottom: 1px solid var(--border);
    background: #fdfdfd;
    border-radius: 16px 16px 0 0;
}

.modal-title {
    font-size: 16px;
    font-weight: 700;
    color: var(--text);
}

.modal-close {
    background: transparent;
    border: none;
    color: var(--text3);
    font-size: 18px;
    cursor: pointer;
    transition: 0.2s;
}

.modal-close:hover {
    color: var(--red);
    transform: scale(1.1);
}

.modal-body {
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.modal-footer {
    padding: 16px 24px;
    border-top: 1px solid var(--border);
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    background: #fdfdfd;
    border-radius: 0 0 16px 16px;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.form-group label {
    font-size: 12.5px;
    font-weight: 600;
    color: var(--text2);
}

.form-control {
    background: var(--surface);
    border: 1px solid var(--border2);
    color: var(--text);
    border-radius: 8px;
    padding: 10px 12px;
    font-size: 13.5px;
    outline: none;
    transition: 0.2s;
}

.form-control:focus {
    border-color: var(--accent);
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

/* ================= DASHBOARD STYLES ================= */
.dashboard-content {
    display: flex;
    flex-direction: column;
    gap: 32px;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
}

.stat-card {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: var(--radius);
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stat-card.active {
    border-left: 4px solid var(--green);
    background: linear-gradient(135deg, rgba(16, 185, 129, 0.05), rgba(16, 185, 129, 0.02));
}

.stat-card.suspended {
    border-left: 4px solid var(--yellow);
    background: linear-gradient(135deg, rgba(245, 158, 11, 0.05), rgba(245, 158, 11, 0.02));
}

.stat-card.pending {
    border-left: 4px solid var(--red);
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.05), rgba(239, 68, 68, 0.02));
}

.stat-icon {
    font-size: 32px;
    width: 60px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--bg3);
    border-radius: 12px;
}

.stat-info {
    flex: 1;
}

.stat-number {
    font-size: 28px;
    font-weight: 800;
    color: var(--text);
    margin-bottom: 4px;
}

.stat-label {
    font-size: 13px;
    color: var(--text2);
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.system-notice {
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.05), rgba(59, 130, 246, 0.02));
    border: 1px solid rgba(59, 130, 246, 0.2);
    border-radius: var(--radius);
    padding: 16px 20px;
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 24px;
}

.notice-icon {
    font-size: 20px;
    flex-shrink: 0;
}

.notice-content {
    font-size: 14px;
    color: var(--text);
    line-height: 1.4;
}

.recent-activity {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: var(--radius);
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.activity-header {
    padding: 20px 24px;
    border-bottom: 1px solid var(--border);
    background: var(--bg3);
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.activity-header h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 700;
    color: var(--text);
}

.activity-list {
    max-height: 400px;
    overflow-y: auto;
}

.activity-item {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    padding: 16px 24px;
    border-bottom: 1px solid var(--border);
    transition: background 0.2s ease;
}

.activity-item:hover {
    background: var(--surface2);
}

.activity-item:last-child {
    border-bottom: none;
}

.activity-icon {
    font-size: 18px;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--bg3);
    border-radius: 8px;
    flex-shrink: 0;
}

.activity-content {
    flex: 1;
    min-width: 0;
}

.activity-message {
    font-size: 14px;
    color: var(--text);
    margin-bottom: 4px;
    line-height: 1.4;
}

.activity-time {
    font-size: 12px;
    color: var(--text3);
    font-weight: 500;
}

.no-activity {
    padding: 40px;
    text-align: center;
    color: var(--text3);
}

/* ================= TOAST ================= */
.toast-container {
    position: fixed;
    bottom: 24px;
    right: 24px;
    z-index: 200;
    display: flex;
    flex-direction: column;
    gap: 10px;
    pointer-events: none;
}

.toast {
    background: var(--surface);
    border: 1px solid var(--border2);
    border-radius: 10px;
    padding: 14px 18px;
    display: flex;
    align-items: center;
    gap: 12px;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
    min-width: 280px;
    max-width: 400px;
    animation: toastIn 0.3s ease;
    transition: opacity 0.3s ease;
    color: var(--text);
    pointer-events: auto;
}

.toast-msg {
    flex: 1;
    font-size: 14px;
    line-height: 1.4;
}

@keyframes toastIn {
    from {
        opacity: 0;
        transform: translateX(100%);
    }
    to {
        opacity: 1;
        transform: translateX(0);
    }
}

.toast.success {
    border-left: 4px solid var(--green);
}

.toast.success::before {
    content: '✅';
}

.toast.error {
    border-left: 4px solid var(--red);
}

.toast.error::before {
    content: '❌';
}

.toast.warning {
    border-left: 4px solid var(--yellow);
}

.toast.warning::before {
    content: '⚠️';
}
    </style>
</head>
<body>

    <!-- ================= SIDEBAR ================= -->
    <aside class="sidebar">
        <div class="sidebar-header">
            <div style="width:32px;height:32px;background:var(--accent);border-radius:8px;display:flex;align-items:center;justify-content:center;color:#fff;">IT</div>
            ADMIN PORTAL
        </div>
        
        <div class="sidebar-nav">
            <div class="nav-section-label">Hệ thống</div>
            <a href="index.jsp" class="nav-item">
                <span class="nav-icon">🏠</span> Trang chủ HMU
            </a>

            <div class="nav-section-label">Tổng quan</div>
            <a class="nav-item" onclick="showModule('dashboard', this)">
                <span class="nav-icon">📊</span> Bảng điều khiển
            </a>
            
            <a href="admin-support" class="nav-item">
                <span class="nav-icon">🎧</span> Yêu cầu hỗ trợ
                <c:if test="${unreadSupportCount > 0}">
                    <span style="background:var(--red); color:#fff; font-size:10px; padding:2px 6px; border-radius:10px; margin-left: auto;">${unreadSupportCount}</span>
                </c:if>
            </a>

            <div class="nav-section-label">Tài khoản Email</div>
            <a class="nav-item active" onclick="showModule('list', this)">
                <span class="nav-icon">📋</span> Danh sách tài khoản
            </a>
            <a class="nav-item" onclick="showModule('revoke', this)">
                <span class="nav-icon">🔒</span> Thu hồi tài khoản
            </a>
            <a class="nav-item" onclick="showModule('archive', this)">
                <span class="nav-icon">📦</span> Bảo lưu tài khoản
            </a>

            <div class="nav-section-label">Quản lý</div>
            <a class="nav-item" onclick="showModule('import', this)">
                <span class="nav-icon">📥</span> Import danh sách
            </a>
            <a class="nav-item" onclick="showModule('notify', this)">
                <span class="nav-icon">📢</span> Gửi thông báo
            </a>
            <a class="nav-item" onclick="showModule('log', this)">
                <span class="nav-icon">📝</span> Nhật ký hoạt động
            </a>
        </div>
        
        <a href="logout" class="logout-btn">
            <span class="nav-icon">🚪</span> Đăng xuất
        </a>
    </aside>

    <!-- ================= MAIN CONTENT ================= -->
    <main class="main-content">
        <header class="topbar">
            <div class="page-title" id="pageTitle">Danh sách tài khoản <span>/ Quản lý email</span></div>
            <div class="user-info">
                <span>Cán bộ: <b>Admin IT</b></span>
                <div class="avatar">A</div>
            </div>
        </header>

        <c:if test="${not empty sessionScope.successMsg}">
            <div id="sessionMessage" data-msg="${sessionScope.successMsg}" style="display:none;"></div>
            <c:remove var="successMsg" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.errorMsg}">
            <div id="sessionErrorMessage" data-msg="${sessionScope.errorMsg}" style="display:none;"></div>
            <c:remove var="errorMsg" scope="session"/>
        </c:if>

        <!-- ================= MODULE 1: DANH SÁCH TÀI KHOẢN ================= -->
        <div class="page active" id="page-list">
            
            <div class="action-bar">
                <div style="display: flex; gap: 10px; align-items: center;">
                    <div style="position: relative; display: flex; align-items: center; gap: 8px;">
                        <div style="position: relative;">
                            <input type="text" id="searchInput" class="search-box" style="width: 280px; padding-left: 36px;" 
                                   placeholder="Tìm tên, MSSV, Email..." oninput="debounceRealtimeSearch()">
                            <span style="position: absolute; left: 12px; top: 9px; color: var(--text3);">🔍</span>
                            <span id="searchLoading" style="position: absolute; right: 12px; top: 9px; display:none;">⏳</span>
                        </div>

                        <div class="filter-container">
                            <button class="btn btn-outline" id="btnFilterToggle" onclick="toggleFilterPanel()">
                                ⚙️ Bộ lọc <span id="filterCountBadge" class="filter-badge">0</span>
                            </button>
                            
                            <!-- BẢNG BỘ LỌC (MODULE) -->
                            <div class="filter-panel" id="filterPanel">
                                <div class="filter-header">
                                    <span class="filter-title">Tùy chọn lọc nâng cao</span>
                                    <button class="modal-close" onclick="toggleFilterPanel()" style="font-size:14px;">✕</button>
                                </div>
                                
                                <div class="form-group">
                                    <label>Trạng thái tài khoản</label>
                                    <select id="statusFilter" class="form-control">
                                        <option value="-1">Tất cả trạng thái</option>
                                        <option value="0">Chờ kích hoạt</option>
                                        <option value="1">Hoạt động</option>
                                        <option value="2">Đang bảo lưu</option>
                                        <option value="3">Chờ xóa</option>
                                    </select>
                                </div>

                                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
                                    <div class="form-group">
                                        <label>Lớp</label>
                                        <input type="text" id="classFilter" class="form-control" placeholder="Tên lớp...">
                                    </div>
                                    <div class="form-group">
                                        <label>Khóa</label>
                                        <input type="text" id="cohortFilter" class="form-control" placeholder="2020...">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label>Khoa / Đơn vị</label>
                                    <input type="text" id="deptFilter" class="form-control" placeholder="Khoa Y...">
                                </div>

                                <div class="form-group">
                                    <label>Ngành học</label>
                                    <input type="text" id="majorFilter" class="form-control" placeholder="Y đa khoa...">
                                </div>

                                <div class="filter-footer">
                                    <button class="btn btn-outline btn-sm" onclick="clearFilters()">Xóa hết</button>
                                    <button class="btn btn-primary btn-sm" onclick="applyFilters()">Áp dụng lọc</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div style="display: flex; gap: 10px; align-items: center;">
                    <form id="autoImportForm" action="import-students" method="POST" enctype="multipart/form-data" style="display: none;">
                        <input type="file" id="excelFileInput" name="excelFile" accept=".xlsx" onchange="confirmAndImport(event)">
                    </form>
                    
                    <button class="btn btn-warning" onclick="showToast('Đồng bộ thành công','success')">🔄 Đồng bộ SSO</button>
                    <button class="btn btn-primary" type="button" onclick="document.getElementById('excelFileInput').click()">📂 Import File (M.01)</button>
                    <button class="btn btn-primary" type="button" onclick="openCreateModal()">➕ Thêm sinh viên</button>
                    <button id="btnResetAll" class="btn btn-danger" type="button" onclick="openResetModal()">♻️ Reset toàn bộ dữ liệu</button>
                    <button class="btn btn-outline" type="button" onclick="showToast('Đang tạo báo cáo Excel...', 'success')">📥 Xuất Excel</button>
                </div>
            </div>

            <div class="table-wrap">
                <div class="table-scroll">
                    <table>
                        <thead>
                            <tr>
                                <th style="width: 40px;">STT</th>
                                <th>Họ và tên</th>
                                <th>Mã SV</th>
                                <th>Giới tính</th>
                                <th>Ngày sinh</th>
                                <th>Lớp</th>
                                <th>Khoa</th>
                                <th>Ngành học</th>
                                <th>Niên khóa</th>
                                <th>Email cá nhân</th>
                                <th>Email được cấp</th>
                                <th>Trạng thái</th>
                                <th style="text-align: right;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="studentTableBody">
                            <c:if test="${empty dsTaiKhoan}">
                                <tr>
                                    <td colspan="13" style="text-align: center; color: var(--text3); padding: 40px;">
                                        📭 Không có dữ liệu tài khoản nào. Vui lòng kiểm tra kết nối database.
                                    </td>
                                </tr>
                            </c:if>
                            <c:forEach items="${dsTaiKhoan}" var="acc" varStatus="loop">
                                <tr>
                                    <td class="mono">${loop.index + 1}</td>
                                    <td class="td-main">${acc.studentName}</td>
                                    <td class="mono">${acc.studentId}</td>
                                    <td>${acc.gender}</td>
                                    <td class="mono">${acc.dateOfBirth}</td>
                                    <td>${acc.className}</td>
                                    <td>${acc.department}</td>
                                    <td>${acc.major}</td>
                                    <td class="mono">${acc.cohort}</td>
                                    <td class="mono" style="font-size: 12px;">${acc.personalEmail}</td>
                                    <td class="mono" style="color:var(--accent2); font-weight:600;">${acc.emailAddress}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${acc.status == 0}"><span class="badge stt-pending"><span class="badge-dot"></span>Chờ kích hoạt</span></c:when>
                                            <c:when test="${acc.status == 1}"><span class="badge stt-active"><span class="badge-dot"></span>Hoạt động</span></c:when>
                                            <c:when test="${acc.status == 2}"><span class="badge stt-suspended"><span class="badge-dot"></span>Đã bảo lưu</span></c:when>
                                            <c:when test="${acc.status == 3}"><span class="badge stt-revoking"><span class="badge-dot"></span>Chờ xóa</span></c:when>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div style="display:flex; gap:6px; justify-content: flex-end;">
                                            <button class="btn btn-outline btn-sm" title="Sửa" onclick="openEditModal(this)"
                                                data-id="${acc.studentId}"
                                                data-name="${acc.studentName}"
                                                data-gender="${acc.gender}"
                                                data-dob="${acc.dateOfBirth}"
                                                data-class="${acc.className}"
                                                data-dept="${acc.department}"
                                                data-major="${acc.major}"
                                                data-cohort="${acc.cohort}"
                                                data-email="${acc.emailAddress}"
                                                data-personal="${acc.personalEmail}"
                                                data-status="${acc.status}">✏️</button>
                                            <c:choose>
                                                <c:when test="${acc.status == 2}"><button class="btn btn-success btn-sm" title="Khôi phục" onclick="restoreAccount('${acc.studentId}')">↩️</button></c:when>
                                                <c:when test="${acc.status == 3}"><button class="btn btn-success btn-sm" title="Hủy xóa" onclick="restoreAccount('${acc.studentId}')">↩️</button></c:when>
                                                <c:otherwise><button class="btn btn-warning btn-sm" title="Khóa" onclick="suspendAccount('${acc.studentId}')">🔒</button></c:otherwise>
                                            </c:choose>
                                            <button class="btn btn-danger btn-sm" title="Xóa" onclick="deleteAccount('${acc.studentId}')">🗑️</button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- CÁC MODULE KHÁC -->
        <!-- ================= MODULE 2: BẢNG ĐIỀU KHIỂN ================= -->
        <div class="page" id="page-dashboard">
            <div class="dashboard-content">
                <!-- Thống kê tổng quan -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-icon">📧</div>
                        <div class="stat-info">
                            <div class="stat-number">${totalAccounts}</div>
                            <div class="stat-label">Tổng tài khoản</div>
                        </div>
                    </div>
                    <div class="stat-card active">
                        <div class="stat-icon">✅</div>
                        <div class="stat-info">
                            <div class="stat-number">${activeAccounts}</div>
                            <div class="stat-label">Đang hoạt động</div>
                        </div>
                    </div>
                    <div class="stat-card suspended">
                        <div class="stat-icon">⏸️</div>
                        <div class="stat-info">
                            <div class="stat-number">${suspendedAccounts}</div>
                            <div class="stat-label">Đang bảo lưu</div>
                        </div>
                    </div>
                    <div class="stat-card pending">
                        <div class="stat-icon">⏳</div>
                        <div class="stat-info">
                            <div class="stat-number">${pendingRevokeAccounts}</div>
                            <div class="stat-label">Chờ thu hồi</div>
                        </div>
                    </div>
                </div>

                <!-- Thông báo hệ thống -->
                <div class="system-notice">
                    <div class="notice-icon">🤖</div>
                    <div class="notice-content">
                        <strong>Hệ thống tự động kích hoạt:</strong> Tài khoản sẽ được tự động kích hoạt sau 24 giờ kể từ ngày tạo. Hệ thống kiểm tra định kỳ mỗi giờ.
                        <button class="btn btn-outline btn-sm" style="margin-left: 12px;" onclick="testAutoActivation()">🔄 Test kích hoạt</button>
                    </div>
                </div>

                <!-- Hoạt động gần đây -->
                <div class="recent-activity">
                    <div class="activity-header">
                        <h3>📝 Hoạt động gần đây</h3>
                        <button class="btn btn-outline btn-sm" onclick="refreshActivity()">🔄 Làm mới</button>
                    </div>
                    <div class="activity-list">
                        <c:forEach var="log" items="${recentLogs}">
                            <div class="activity-item">
                                <div class="activity-icon">
                                    <c:choose>
                                        <c:when test="${log.actionType == 'CREATE'}">➕</c:when>
                                        <c:when test="${log.actionType == 'ACTIVATE'}">✅</c:when>
                                        <c:when test="${log.actionType == 'AUTO_ACTIVATE'}">🤖</c:when>
                                        <c:when test="${log.actionType == 'SUSPEND'}">⏸️</c:when>
                                        <c:when test="${log.actionType == 'DELETE'}">🗑️</c:when>
                                        <c:when test="${log.actionType == 'SUSPEND_BATCH'}">📦</c:when>
                                        <c:when test="${log.actionType == 'SUSPEND_ERROR'}">❌</c:when>
                                        <c:when test="${log.actionType == 'BATCH_RESULT'}">📋</c:when>
                                        <c:otherwise>📝</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="activity-content">
                                    <div class="activity-message">
                                        <strong>${log.actionType}</strong> tài khoản <span class="mono">${log.targetEmail}</span>
                                        <c:if test="${not empty log.reason}"> - ${log.reason}</c:if>
                                    </div>
                                    <div class="activity-time">
                                        <fmt:formatDate value="${log.actionTime}" pattern="dd/MM/yyyy HH:mm"/>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                        <c:if test="${empty recentLogs}">
                            <div class="no-activity">
                                <div style="text-align: center; color: var(--text3); padding: 40px;">
                                    📭 Chưa có hoạt động nào
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
        <div class="page" id="page-revoke"><div style="margin:auto; color:var(--text3);"><h2>🔒 Giao diện Thu hồi tài khoản</h2></div></div>
        <div class="page" id="page-archive">
            <div class="action-bar" style="margin-bottom: 20px;">
                <div style="flex: 1;">
                    <h2 class="page-title">📦 Bảo lưu tài khoản hàng loạt</h2>
                    <p style="color: var(--text2); margin-top: 8px; font-size: 13px;">Tải lên danh sách sinh viên (Excel) cần bảo lưu tài khoản. Cấu trúc yêu cầu: Cột 1 (Họ tên), Cột 2 (Email), Cột 3 (Mã SV), Cột 4 (Niên khóa).</p>
                </div>
            </div>
            <div class="table-wrap" style="padding: 30px; display: flex; flex-direction: column; align-items: center; justify-content: center; background: var(--surface);">
                <form id="batchSuspendForm" action="batch-suspend" method="POST" enctype="multipart/form-data" style="display: flex; flex-direction: column; gap: 20px; align-items: center; max-width: 400px; width: 100%;">
                    <div style="width: 100%; padding: 40px 20px; border: 2px dashed var(--border2); border-radius: var(--radius); text-align: center; background: var(--bg3); transition: 0.2s; cursor: pointer;" onclick="document.getElementById('suspendExcelInput').click()" onmouseover="this.style.borderColor='var(--accent)';" onmouseout="this.style.borderColor='var(--border2)';">
                        <div style="font-size: 40px; margin-bottom: 10px;">📁</div>
                        <div style="font-weight: 600; color: var(--text);">Nhấn để chọn file Excel (.xlsx)</div>
                        <div style="font-size: 12px; color: var(--text3); margin-top: 5px;" id="suspendFileName">Chưa có file nào được chọn</div>
                    </div>
                    <input type="file" id="suspendExcelInput" name="excelFile" accept=".xlsx, .xls" style="display: none;" onchange="document.getElementById('suspendFileName').innerText = this.files[0] ? this.files[0].name : 'Chưa có file nào được chọn';">
                    
                    <button type="submit" class="btn btn-primary" style="width: 100%; justify-content: center; padding: 12px; font-size: 14px;" onclick="if(!document.getElementById('suspendExcelInput').value) { alert('Vui lòng chọn file Excel trước!'); return false; } this.innerHTML = '⏳ Đang xử lý...'; this.style.opacity = '0.7';">
                        Bắt đầu xử lý hàng loạt
                    </button>
                </form>
            </div>
        </div>
        <div class="page" id="page-import"><div style="margin:auto; color:var(--text3);"><h2>📥 Giao diện Quản lý file Import</h2></div></div>
        <div class="page" id="page-notify"><div style="margin:auto; color:var(--text3);"><h2>📢 Giao diện Gửi thông báo</h2></div></div>
        <div class="page" id="page-log">
            <div class="action-bar" style="margin-bottom: 20px;">
                <div style="flex: 1;">
                    <h2 class="page-title">📝 Nhật ký hoạt động toàn hệ thống</h2>
                    <p style="color: var(--text2); margin-top: 8px; font-size: 13px;">Theo dõi tất cả các thay đổi và tác vụ vừa được thực thi.</p>
                </div>
                <button class="btn btn-outline" onclick="refreshActivity()">🔄 Làm mới</button>
            </div>
            <div class="table-wrap">
                <div class="table-scroll">
                    <table>
                        <thead>
                            <tr>
                                <th style="width: 150px;">Thời gian</th>
                                <th style="width: 150px;">Loại tác vụ</th>
                                <th>Mục tiêu (Email/Hệ thống)</th>
                                <th>Chi tiết / Kết quả</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="log" items="${allLogs}">
                                <tr>
                                    <td><fmt:formatDate value="${log.actionTime}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                    <td>
                                        <span class="badge" style="background:var(--surface2); color:var(--text);">
                                            <c:choose>
                                                <c:when test="${log.actionType == 'ACTIVATE'}">✅</c:when>
                                                <c:when test="${log.actionType == 'AUTO_ACTIVATE'}">🤖</c:when>
                                                <c:when test="${log.actionType == 'SUSPEND'}">⏸️</c:when>
                                                <c:when test="${log.actionType == 'DELETE'}">🗑️</c:when>
                                                <c:when test="${log.actionType == 'SUSPEND_BATCH'}">📦</c:when>
                                                <c:when test="${log.actionType == 'SUSPEND_ERROR'}">❌</c:when>
                                                <c:when test="${log.actionType == 'BATCH_RESULT'}">📋</c:when>
                                                <c:otherwise>📝</c:otherwise>
                                            </c:choose>
                                            ${log.actionType}
                                        </span>
                                    </td>
                                    <td class="mono" style="color: var(--accent2);">${log.targetEmail}</td>
                                    <td>${log.reason}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty allLogs}">
                                <tr>
                                    <td colspan="4" style="text-align: center; padding: 40px; color: var(--text3);">
                                        📭 Chưa có hoạt động nào được ghi nhận.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </main>

    <!-- ================= MODAL SỬA THÔNG TIN ================= -->
    <div class="modal-overlay" id="editModal">
        <div class="modal">
            <div class="modal-header">
                <div class="modal-title">✏️ Chỉnh sửa thông tin Sinh viên</div>
                <button class="modal-close" onclick="closeEditModal()">✕</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>Mã sinh viên (Không thể sửa)</label>
                    <input type="text" class="form-control mono" id="editStuId" readonly style="background:var(--bg3); color:var(--text3); cursor:not-allowed;">
                </div>
                <div class="form-group">
                    <label>Họ và tên</label>
                    <input type="text" class="form-control" id="editStuName">
                </div>
                <div class="form-group">
                    <label>Ngày sinh</label>
                    <input type="date" class="form-control" id="editStuDob">
                </div>
                <div class="form-group">
                    <label>Giới tính</label>
                    <select class="form-control" id="editStuGender">
                        <option value="">Chọn giới tính</option>
                        <option value="Nam">Nam</option>
                        <option value="Nữ">Nữ</option>
                        <option value="Khác">Khác</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Tên lớp</label>
                    <input type="text" class="form-control" id="editStuClass">
                </div>
                <div class="form-group">
                    <label>Khoa</label>
                    <input type="text" class="form-control" id="editStuDept">
                </div>
                <div class="form-group">
                    <label>Ngành học</label>
                    <input type="text" class="form-control" id="editStuMajor">
                </div>
                <div class="form-group">
                    <label>Niên khóa</label>
                    <input type="text" class="form-control" id="editStuCohort">
                </div>
                <div class="form-group">
                    <label>Email cá nhân</label>
                    <input type="email" class="form-control" id="editStuPersonalEmail">
                </div>
                <div class="form-group">
                    <label>Email được cấp</label>
                    <input type="email" class="form-control mono" id="editStuEmail">
                </div>
                <div class="form-group">
                    <label>Trạng thái tài khoản</label>
                    <select class="form-control" id="editStuStatus">
                        <option value="1">Đang hoạt động</option>
                        <option value="0">Chờ kích hoạt</option>
                        <option value="2">Tạm khóa / Bảo lưu</option>
                        <option value="3">Chờ xóa</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-outline" onclick="closeEditModal()">Hủy bỏ</button>
                <button class="btn btn-primary" onclick="submitEdit()">💾 Lưu thay đổi</button>
            </div>
        </div>
    </div>

    <!-- ================= MODAL THÊM SINH VIÊN MỚI ================= -->
    <div class="modal-overlay" id="createModal">
        <div class="modal">
            <div class="modal-header">
                <div class="modal-title">➕ Thêm sinh viên mới</div>
                <button class="modal-close" onclick="closeCreateModal()">✕</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>Mã sinh viên</label>
                    <input type="text" class="form-control mono" id="newStuId" placeholder="VD: SV12345">
                </div>
                <div class="form-group">
                    <label>Họ và tên</label>
                    <input type="text" class="form-control" id="newStuName" placeholder="Nguyễn Văn A">
                </div>
                <div class="form-group">
                    <label>Ngày sinh</label>
                    <input type="date" class="form-control" id="newStuDob">
                </div>
                <div class="form-group">
                    <label>Giới tính</label>
                    <select class="form-control" id="newStuGender">
                        <option value="">Chọn giới tính</option>
                        <option value="Nam">Nam</option>
                        <option value="Nữ">Nữ</option>
                        <option value="Khác">Khác</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Tên lớp</label>
                    <input type="text" class="form-control" id="newStuClass" placeholder="Ví dụ: K15 Y Đa khoa">
                </div>
                <div class="form-group">
                    <label>Khoa / Đơn vị</label>
                    <input type="text" class="form-control" id="newStuDept" placeholder="Ví dụ: Khoa Y">
                </div>
                <div class="form-group">
                    <label>Ngành học</label>
                    <input type="text" class="form-control" id="newStuMajor" placeholder="Ví dụ: Y đa khoa">
                </div>
                <div class="form-group">
                    <label>Niên khóa</label>
                    <input type="text" class="form-control" id="newStuCohort" placeholder="Ví dụ: 2020-2026">
                </div>
                <div class="form-group">
                    <label>Email cá nhân</label>
                    <input type="email" class="form-control" id="newStuPersonalEmail" placeholder="email@example.com">
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-outline" onclick="closeCreateModal()">Hủy bỏ</button>
                <button class="btn btn-primary" onclick="submitCreate()">💾 Tạo sinh viên</button>
            </div>
        </div>
    </div>

    <!-- ================= MODAL RESET TOÀN BỘ DỮ LIỆU ================= -->
    <div class="modal-overlay" id="resetModal">
        <div class="modal">
            <div class="modal-header">
                <div class="modal-title">⚠️ Reset toàn bộ dữ liệu sinh viên</div>
                <button class="modal-close" onclick="closeResetModal()">✕</button>
            </div>
            <div class="modal-body">
                <p>Bạn sắp xóa toàn bộ sinh viên và email hiện tại. Hành động này không thể hoàn tác.</p>
                <form id="resetForm" action="reset-data" method="POST" onsubmit="return confirmReset(event)">
                    <div class="form-group">
                        <label>Xác nhận mật khẩu admin</label>
                        <input type="password" class="form-control" name="adminPassword" placeholder="Nhập mật khẩu admin" required>
                    </div>
                    <div class="form-group">
                        <label>Ghi chú</label>
                        <textarea class="form-control" rows="3" readonly>Hệ thống sẽ xóa toàn bộ dữ liệu trong bảng students và email_accounts trước khi import lại.</textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline" onclick="closeResetModal()">Hủy bỏ</button>
                        <button type="submit" class="btn btn-danger">Xác nhận reset</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- TOAST CONTAINER -->
    <div class="toast-container" id="toastContainer"></div>

    <script>
        // Chỉ giữ lại biến này để lấy đường dẫn gốc từ Tomcat (ví dụ: /email-management)
        // Biến này sẽ được dùng trong hàm fetch của file main.js
        const contextPath = '${pageContext.request.contextPath}'; 

        // Function để refresh activity logs
        function refreshActivity() {
            // Reload the page to get fresh data
            window.location.reload();
        }

        // Reset modal helpers
        function openResetModal() {
            const resetModal = document.getElementById('resetModal');
            if (resetModal) {
                resetModal.classList.add('open');
            }
        }

        function closeResetModal() {
            const resetModal = document.getElementById('resetModal');
            if (resetModal) {
                resetModal.classList.remove('open');
            }
        }

        function confirmReset(event) {
            if (!confirm('Bạn có chắc chắn muốn reset toàn bộ dữ liệu sinh viên? Hành động này sẽ xóa tất cả dữ liệu hiện tại và không thể hoàn tác.')) {
                event.preventDefault();
                return false;
            }
            return true;
        }
    </script>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>