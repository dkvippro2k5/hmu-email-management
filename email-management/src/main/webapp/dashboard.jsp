<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Email Management - HMU</title>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
    
    <script>
        // Xử lý Hash URL ngay lập tức trước khi render DOM để loại bỏ hoàn toàn hiện tượng chớp nháy (Flash)
        document.addEventListener('DOMContentLoaded', function() {
            let hash = window.location.hash.substring(1);
            let modName = (hash && hash.startsWith('page-')) ? hash.replace('page-', '') : 'list';
            
            document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
            document.querySelectorAll('.nav-item').forEach(nav => nav.classList.remove('active'));
            
            let activePage = document.getElementById('page-' + modName);
            if(activePage) activePage.classList.add('active');
            
            let activeNav = document.querySelector('.nav-item[onclick*="' + modName + '"]');
            if(activeNav) activeNav.classList.add('active');
            
            const titles = {
                dashboard: 'Bảng điều khiển <span>/ Tổng quan hệ thống</span>',
                list: 'Danh sách tài khoản <span>/ Quản lý email</span>',
                revoke: 'Thu hồi tài khoản <span>/ Xử lý thu hồi</span>',
                archive: 'Bảo lưu tài khoản <span>/ Quản lý bảo lưu</span>',
                import: 'Import danh sách <span>/ Quản lý tệp dữ liệu</span>',
                log: 'Nhật ký hoạt động <span>/ Log hệ thống</span>',
                support: 'Support <span>/ Yêu cầu hỗ trợ</span>'
            };
            let titleEl = document.getElementById('pageTitle');
            if (titleEl) {
                titleEl.innerHTML = titles[modName] || modName;
            }
        });
    </script>
    
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
            
            <a class="nav-item" onclick="showModule('support', this)">
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
            <div class="user-info" style="display: flex; gap: 15px; align-items: center;">
                <button class="btn btn-warning" style="padding: 6px 12px; font-size: 13px;" onclick="showToast('Đồng bộ thành công','success')">🔄 Đồng bộ SSO</button>
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
        <div class="page" id="page-list">
            
            <div class="action-bar">
                <div style="display: flex; gap: 10px; align-items: center;">
                    <div style="position: relative; display: flex; align-items: center; gap: 10px;">
                        <div style="position: relative;">
                            <input type="text" id="searchInput" class="search-box" style="width: 280px; padding-left: 36px;" 
                                   placeholder="Tìm tên, MSSV..." oninput="debounceRealtimeSearch()">
                            <span style="position: absolute; left: 12px; top: 9px; color: var(--text3);">🔍</span>
                            <span id="searchLoading" style="position: absolute; right: 12px; top: 9px; display:none;">⏳</span>
                        </div>
                        <select id="statusFilter" class="input" style="padding: 6px 10px; border-radius: 6px; border: 1px solid var(--border);" onchange="triggerApiSearch()">
                            <option value="-1">Tất cả trạng thái</option>
                            <option value="0">Chờ kích hoạt</option>
                            <option value="1">Hoạt động</option>
                            <option value="2">Đang bảo lưu</option>
                            <option value="3">Chờ xóa</option>
                        </select>
                    </div>
                </div>

                <div style="display: flex; gap: 10px; align-items: center;">
                    <form id="autoImportForm" action="import-excel" method="POST" enctype="multipart/form-data" style="display: none;">
                        <input type="file" id="excelFileInput" name="excelFile" accept=".xlsx" onchange="confirmAndImport(event)">
                    </form>
                    
                    <button class="btn btn-primary" type="button" onclick="document.getElementById('excelFileInput').click()">📂 Import File (M.01)</button>
                    <button class="btn btn-primary" type="button" onclick="openCreateModal()">➕ Thêm sinh viên</button>
                    <button class="btn btn-outline" type="button" onclick="openExportModal()">📥 Xuất Excel</button>
                </div>
            </div>

            <div class="table-wrap">
                <div class="table-scroll">
                    <table>
                        <thead>
                            <tr>
                                <th style="width: 40px;">STT</th>
                                <th>Họ và tên</th>
                                <th>CCCD</th>
                                <th>Mã SV</th>
                                <th>Niên khóa</th>
                                <th>SĐT</th>
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
                                    <td class="mono">${acc.cccd}</td>
                                    <td class="mono">${acc.studentId}</td>
                                    <td class="mono">${acc.cohort}</td>
                                    <td class="mono">${acc.phoneNumber}</td>
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
                                                data-cccd="${acc.cccd}"
                                                data-first-name="${acc.firstName}"
                                                data-last-name="${acc.lastName}"
                                                data-cohort="${acc.cohort}"
                                                data-phone="${acc.phoneNumber}"
                                                data-email="${acc.emailAddress}"
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
                        <c:set var="lastDate" value="" />
                        <c:forEach var="log" items="${recentLogs}">
                            <fmt:formatDate value="${log.actionTime}" pattern="dd/MM/yyyy" var="currentDate" />
                            <c:if test="${currentDate != lastDate}">
                                <div class="date-header" style="padding: 8px 15px; font-weight: 600; color: var(--text2); background: var(--surface2); margin: 15px 0 5px; border-radius: 6px; font-size: 13px;">
                                    📅 ${currentDate == fn:substring(sessionScope.todayStr, 0, 10) ? 'Hôm nay' : currentDate}
                                </div>
                                <c:set var="lastDate" value="${currentDate}" />
                            </c:if>
                            <div class="activity-item">
                                <div class="activity-icon">
                                    <c:choose>
                                        <c:when test="${log.actionType == 'CREATE'}">➕</c:when>
                                        <c:when test="${log.actionType == 'EDIT'}">✏️</c:when>
                                        <c:when test="${log.actionType == 'ACTIVATE'}">✅</c:when>
                                        <c:when test="${log.actionType == 'AUTO_ACTIVATE'}">🤖</c:when>
                                        <c:when test="${log.actionType == 'SUSPEND'}">⏸️</c:when>
                                        <c:when test="${log.actionType == 'DELETE'}">🗑️</c:when>
                                        <c:when test="${log.actionType == 'BATCH_IMPORT'}">📥</c:when>
                                        <c:when test="${log.actionType == 'BATCH_SUSPEND'}">📦</c:when>
                                        <c:when test="${log.actionType == 'BATCH_REVOKE'}">🚫</c:when>
                                        <c:when test="${log.actionType == 'RESTORE'}">↩️</c:when>
                                        <c:when test="${log.actionType == 'SUSPEND_ERROR'}">❌</c:when>
                                        <c:otherwise>📝</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="activity-content">
                                    <div class="activity-message">
                                        <strong>${log.actionType}</strong> - <span class="mono"><c:out value="${empty log.targetEmail ? '[Hàng loạt]' : log.targetEmail}"/></span>
                                        <c:if test="${not empty log.reason}"> - ${log.reason}</c:if>
                                        <c:if test="${not empty log.details}">
                                            <button class="btn btn-outline" style="padding: 2px 8px; font-size: 11px; margin-left: 8px;" onclick="viewLogDetails('${fn:escapeXml(log.details)}')">👁️ Chi tiết</button>
                                        </c:if>
                                    </div>
                                    <div class="activity-time">
                                        <fmt:formatDate value="${log.actionTime}" pattern="HH:mm"/>
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
        <!-- TRANG THU HỒI TÀI KHOẢN -->
        <div class="page" id="page-revoke">
            <div class="page-header" style="flex-wrap: wrap; gap: 15px;">

                
                <div style="display: flex; gap: 10px; align-items: center; background: var(--surface); padding: 10px 15px; border-radius: 10px; border: 1px solid var(--border); width: 100%; max-width: 600px; margin-right: auto;">
                    <form action="${pageContext.request.contextPath}/batch-revoke" method="post" enctype="multipart/form-data" style="display: flex; align-items: center; gap: 10px; margin: 0; width: 100%;">
                        <input type="text" name="decisionNumber" placeholder="Nhập Số QĐ (Bắt buộc chứa /QĐ-ĐHYHN)" required class="input" style="flex: 1; padding: 6px; border: 1px solid var(--border); border-radius: 6px;">
                        <input type="file" id="revokeExcelInput" name="excelFile" accept=".xls,.xlsx" required style="display: none;" onchange="if(this.files[0]) { document.getElementById('submitRevokeBtn').style.display='block'; document.getElementById('uploadRevokeLabelBtn').innerText = 'Đã chọn: ' + this.files[0].name; }">
                        <button type="button" class="btn btn-outline" id="uploadRevokeLabelBtn" onclick="document.getElementById('revokeExcelInput').click()" style="border-color: var(--accent); color: var(--accent); white-space: nowrap;">📁 Chọn Excel</button>
                        <button type="submit" class="btn btn-primary" id="submitRevokeBtn" style="display: none; white-space: nowrap;" onclick="this.innerHTML = '⏳ Đang xử lý...'; this.style.opacity = '0.7';">Bắt đầu xử lý</button>
                    </form>
                </div>
                
                <button class="btn" style="background: var(--surface); border: 1px solid var(--border); margin-left: auto;" onclick="exportRevokedToPDF()">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" style="margin-right:6px; color:var(--red)"><path fill-rule="evenodd" d="M14 4.5V14a2 2 0 0 1-2 2h-1v-1h1a1 1 0 0 0 1-1V4.5h-2A1.5 1.5 0 0 1 9.5 3V1H4a1 1 0 0 0-1 1v9H2V2a2 2 0 0 1 2-2h5.5L14 4.5ZM1.6 11.85H0v3.999h.791v-1.342h.803c.287 0 .531-.057.732-.173.203-.117.358-.275.463-.474a1.42 1.42 0 0 0 .161-.677c0-.25-.053-.476-.158-.677a1.176 1.176 0 0 0-.46-.477c-.2-.12-.443-.179-.732-.179Zm.545 1.333a.795.795 0 0 1-.085.38.574.574 0 0 1-.238.241.794.794 0 0 1-.375.082H.788V12.48h.66c.218 0 .389.06.512.181.123.122.185.296.185.522Zm1.217-1.333v3.999h1.46c.401 0 .734-.08.998-.237a1.45 1.45 0 0 0 .595-.689c.13-.3.196-.662.196-1.084 0-.42-.065-.778-.196-1.075a1.426 1.426 0 0 0-.589-.68c-.264-.156-.599-.234-1.005-.234H3.362Zm.791.645h.563c.249 0 .45.05.603.151.155.101.264.246.326.435.062.188.093.414.093.678 0 .265-.031.492-.093.68-.062.188-.171.333-.326.434-.153.1-.354.152-.603.152h-.563v-2.53ZM6.764 11.85v3.999h.791v-1.632h1.474v-.645H7.555v-1.077h1.666v-.645H6.764Z"/></svg>
                    Xuất danh sách PDF
                </button>
            </div>

            <!-- Khối hướng dẫn -->
            <div style="background: rgba(245, 158, 11, 0.05); border: 1px solid rgba(245, 158, 11, 0.2); border-left: 4px solid #d97706; border-radius: 8px; padding: 16px; margin: 0 24px 24px 24px;">
                <h4 style="color: #b45309; margin-bottom: 8px; font-weight: 600;">⚠️ Quy định Thu hồi tài khoản</h4>
                <ul style="margin-left: 20px; color: var(--text2); line-height: 1.6; font-size: 14px;">
                    <li><strong style="color: var(--text);">Thông báo tự động:</strong> Hệ thống tự động gửi mail thông báo đến học viên/sinh viên về việc thu hồi email tên miền <em>hmu.edu.vn</em>.</li>
                    <li><strong style="color: var(--text);">Thời hạn xóa vĩnh viễn:</strong> Sau <strong>30 ngày</strong> tính từ ngày hệ thống ghi nhận thu hồi (upload danh sách), tài khoản email sẽ tự động bị xóa khỏi hệ thống.</li>
                    <li><strong style="color: var(--text);">Yêu cầu sao lưu:</strong> Đề nghị học viên/sinh viên backup (sao lưu) dữ liệu sang địa chỉ mail khác hoặc lưu trữ về máy tính cá nhân (PL.01.CNTT&TT.09).</li>
                    <li style="margin-top: 8px; list-style: none;">
                        <div style="background: var(--surface); padding: 10px 15px; border-radius: 6px; border: 1px solid var(--border); display: inline-block;">
                            <strong style="color: var(--text);">📞 Hỗ trợ kỹ thuật:</strong> Phòng CNTT&TT - P.320, tầng 3, nhà A1<br>
                            <strong style="color: var(--text);">ĐT:</strong> 024 38523798 (#3198) &nbsp;|&nbsp; <strong style="color: var(--text);">Email:</strong> support@hmu.edu.vn
                        </div>
                    </li>
                </ul>
            </div>

            <!-- Bảng danh sách chờ xóa -->
            <div class="card" style="margin: 0 24px 24px 24px;">
                <div style="padding: 20px 24px; border-bottom: 1px solid var(--border); display: flex; justify-content: space-between; align-items: center;">
                    <h3 style="font-size: 15px; font-weight: 700; color: var(--text);">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" style="margin-right: 8px; color: var(--text3);"><path d="M14 1a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h12zM2 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2H2z"/><path d="M4 4.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-7a.5.5 0 0 1-.5-.5v-1zM4 8.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-7a.5.5 0 0 1-.5-.5v-1z"/></svg>
                        Danh sách tài khoản chờ thu hồi
                    </h3>
                    
                    <div style="display: flex; gap: 10px; align-items: center;">
                        <div style="position: relative;">
                            <input type="text" id="revokeSearchInput" class="search-box" style="width: 280px; padding-left: 36px;" 
                                   placeholder="Tìm tên, MSSV..." oninput="debounceRevokeSearch()">
                            <span style="position: absolute; left: 12px; top: 9px; color: var(--text3);">🔍</span>
                            <span id="revokeSearchLoading" style="position: absolute; right: 12px; top: 9px; display:none;">⏳</span>
                        </div>
                    </div>
                </div>

                <div class="table-container">
                    <table class="table" id="revokeTable">
                        <thead>
                            <tr>
                                <th>Địa chỉ Email</th>
                                <th>Mã Sinh Viên</th>
                                <th>Họ & Tên</th>
                                <th>Lớp</th>
                                <th>Ngày tạo</th>
                                <th>Lịch xóa</th>
                                <th>Trạng thái</th>
                                <th style="text-align: right; width: 100px;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="revokeTbody">
                            <c:forEach var="acc" items="${dsThuHoi}">
                                <tr>
                                    <td><div style="font-weight: 500;">${acc.emailAddress}</div></td>
                                    <td style="color: var(--text2);">${acc.studentId}</td>
                                    <td>${acc.studentName}</td>
                                    <td>${acc.cohort}</td>
                                    <td><fmt:formatDate value="${acc.activationDate}" pattern="dd/MM/yyyy" /></td>
                                    <td>
                                        <c:if test="${not empty acc.scheduledDeleteDate}">
                                            <span style="color: #d97706; font-weight: 500; display: inline-flex; align-items: center; gap: 4px;">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z"/><path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z"/></svg>
                                                <fmt:formatDate value="${acc.scheduledDeleteDate}" pattern="dd/MM/yyyy" />
                                            </span>
                                        </c:if>
                                        <c:if test="${empty acc.scheduledDeleteDate}">
                                            <span style="color: var(--text3);">Chưa lên lịch</span>
                                        </c:if>
                                    </td>
                                    <td><span class="badge" style="background: rgba(245, 158, 11, 0.1); color: #d97706; border-color: rgba(245, 158, 11, 0.2);">Chờ xóa</span></td>
                                    <td style="text-align: right;">
                                        <button class="action-btn" title="Hủy xóa (Khôi phục)" onclick="restoreAccount('${acc.studentId}')" style="color: #059669; background: rgba(16, 185, 129, 0.1);">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16"><path d="M9.302 1.256a1.5 1.5 0 0 0-2.604 0l-1.704 2.98a.5.5 0 0 0 .869.497l1.703-2.981a.5.5 0 0 1 .868 0l2.54 4.444-1.256-.337a.5.5 0 1 0-.26.966l2.415.647a.5.5 0 0 0 .613-.353l.647-2.415a.5.5 0 1 0-.966-.259l-.333 1.242-2.532-4.431zM2.973 7.773l-1.255.337a.5.5 0 1 1-.26-.966l2.416-.647a.5.5 0 0 1 .612.353l.647 2.415a.5.5 0 0 1-.966.259l-.333-1.242-2.545 4.454a.5.5 0 0 0 .434.748H5a.5.5 0 0 1 0 1H1.723A1.5 1.5 0 0 1 .421 12.24l2.552-4.467zm10.89 1.463a.5.5 0 1 0-.868.496l1.716 3.004a.5.5 0 0 1-.434.748h-2.56l.34-.85a.5.5 0 1 0-.928-.372l-.636 1.58a.5.5 0 0 0 .373.636l1.58.636a.5.5 0 0 0 .372-.928l-.855-.34h2.56a1.5 1.5 0 0 0 1.302-2.244l-1.716-3.004z"/></svg>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="page" id="page-archive">
            <div class="action-bar" style="margin-bottom: 20px;">

                
                <form id="batchSuspendForm" action="batch-suspend" method="POST" enctype="multipart/form-data" style="display: flex; align-items: center; gap: 10px; margin: 0; margin-right: auto; width: 600px;">
                    <input type="text" name="decisionNumber" placeholder="Số QĐ (Có /QĐ-ĐHYHN)" required class="input" style="flex: 1; padding: 6px; border: 1px solid var(--border); border-radius: 6px;">
                    <input type="file" id="suspendExcelInput" name="excelFile" accept=".xlsx, .xls" style="display: none;" onchange="if(this.files[0]) { document.getElementById('submitBatchBtn').style.display='block'; document.getElementById('uploadLabelBtn').innerText = 'Đã chọn: ' + this.files[0].name; }">
                    <button type="button" class="btn btn-outline" id="uploadLabelBtn" onclick="document.getElementById('suspendExcelInput').click()" style="border-color: var(--accent); color: var(--accent); white-space: nowrap;">📁 Chọn Excel</button>
                    <button type="submit" class="btn btn-primary" id="submitBatchBtn" style="display: none; white-space: nowrap;" onclick="this.innerHTML = '⏳ Đang xử lý...'; this.style.opacity = '0.7';">Bắt đầu xử lý</button>
                </form>

                <button class="btn btn-outline" style="border-color: #e74c3c; color: #e74c3c; margin-left: 10px;" onclick="exportSuspendedToPDF()">📄 Xuất PDF (Hồ sơ)</button>
            </div>
            
            <!-- SEARCH AND FILTER BAR FOR SUSPENDED LIST -->
            <div class="action-bar" style="padding: 10px 0; background: transparent; box-shadow: none;">
                <div style="display: flex; gap: 10px; align-items: center;">
                    <div style="position: relative;">
                        <input type="text" id="suspendedSearchInput" class="search-box" style="width: 280px; padding-left: 36px;" 
                               placeholder="Tìm tên, MSSV..." oninput="debounceSuspendedSearch()">
                        <span style="position: absolute; left: 12px; top: 9px; color: var(--text3);">🔍</span>
                        <span id="suspendedSearchLoading" style="position: absolute; right: 12px; top: 9px; display:none;">⏳</span>
                    </div>
                </div>
            </div>

            <div class="table-wrap">
                <div class="table-scroll">
                    <table id="suspendedTable">
                        <thead>
                            <tr>
                                <th>Email</th>
                                <th>Mã SV</th>
                                <th>Họ Tên</th>
                                <th>Lớp</th>
                                <th>Ngày bảo lưu</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody id="suspendedTbody">
                            <c:forEach var="acc" items="${dsBaoLuu}">
                                <tr>
                                    <td><div style="font-weight: 500;">${acc.emailAddress}</div></td>
                                    <td style="color: var(--text2);">${acc.studentId}</td>
                                    <td>${acc.studentName}</td>
                                    <td>${acc.cohort}</td>
                                    <td><fmt:formatDate value="${acc.activationDate}" pattern="dd/MM/yyyy"/></td>
                                    <td><span class="badge" style="background: rgba(231, 76, 60, 0.15); color: #e74c3c;">Đã bảo lưu</span></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty dsBaoLuu}">
                                <tr>
                                    <td colspan="6" style="text-align: center; padding: 30px; color: var(--text3);">Không có tài khoản nào đang bảo lưu</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="page" id="page-support">
            <div class="page-header" style="margin-bottom: 20px;">
                <div>
                    <h2 class="page-title" style="margin-bottom: 20px;">Yêu cầu hỗ trợ</h2>
                </div>
            </div>
            <div class="table-wrap">
                <div class="table-scroll">
                    <table>
                        <thead>
                            <tr>
                                <th style="width: 60px;">ID</th>
                                <th>Sinh viên</th>
                                <th>Chủ đề</th>
                                <th>Nội dung</th>
                                <th>Ngày gửi</th>
                                <th>Trạng thái</th>
                                <th style="text-align: right;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty dsHoTro}">
                                <tr>
                                    <td colspan="7" style="text-align: center; color: var(--text3); padding: 40px;">
                                        📭 Không có yêu cầu hỗ trợ nào.
                                    </td>
                                </tr>
                            </c:if>
                            <c:forEach items="${dsHoTro}" var="r">
                                <tr>
                                    <td class="mono">${r.requestId}</td>
                                    <td><strong><c:out value="${empty r.studentName ? 'Không rõ' : r.studentName}"/></strong><br><span style="font-size: 12px; color: var(--text3);">${r.studentId}</span></td>
                                    <td><strong>${r.subject}</strong></td>
                                    <td style="max-width: 300px; white-space: normal;">${r.content}</td>
                                    <td class="mono">${r.createdAt}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${r.status == 0}">
                                                <span class="badge" style="background: rgba(245, 158, 11, 0.1); color: #b45309;">
                                                    <span class="badge-dot" style="background: #f59e0b;"></span>Đang chờ
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge" style="background: rgba(16, 185, 129, 0.1); color: #047857;">
                                                    <span class="badge-dot" style="background: #10b981;"></span>Đã xử lý
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="text-align: right;">
                                        <c:if test="${r.status == 0}">
                                            <a href="admin-support?action=resolve&id=${r.requestId}" class="btn btn-success btn-sm">✅ Xác nhận xử lý</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" style="margin-right:6px;"><path d="M15.964.686a.5.5 0 0 0-.65-.65L.767 5.855H.766l-.452.18a.5.5 0 0 0-.082.887l.41.26.001.002 4.995 3.178 3.178 4.995.002.002.26.41a.5.5 0 0 0 .886-.083l6-15Zm-1.833 1.89L6.637 10.07l-.215-.338a.5.5 0 0 0-.154-.154l-.338-.215 7.494-7.494 1.178-.471-.47 1.178Z"/></svg>
                            Gửi Thông Báo
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div class="page" id="page-log">
            <div class="action-bar" style="margin-bottom: 20px;">

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
                                                <c:when test="${log.actionType == 'CREATE'}">➕</c:when>
                                                <c:when test="${log.actionType == 'EDIT'}">✏️</c:when>
                                                <c:when test="${log.actionType == 'ACTIVATE'}">✅</c:when>
                                                <c:when test="${log.actionType == 'AUTO_ACTIVATE'}">🤖</c:when>
                                                <c:when test="${log.actionType == 'SUSPEND'}">⏸️</c:when>
                                                <c:when test="${log.actionType == 'DELETE'}">🗑️</c:when>
                                                <c:when test="${log.actionType == 'BATCH_IMPORT'}">📥</c:when>
                                                <c:when test="${log.actionType == 'BATCH_SUSPEND'}">📦</c:when>
                                                <c:when test="${log.actionType == 'BATCH_REVOKE'}">🚫</c:when>
                                                <c:when test="${log.actionType == 'RESTORE'}">↩️</c:when>
                                                <c:when test="${log.actionType == 'SUSPEND_ERROR'}">❌</c:when>
                                                <c:otherwise>📝</c:otherwise>
                                            </c:choose>
                                            ${log.actionType}
                                        </span>
                                    </td>
                                    <td class="mono" style="color: var(--accent2);"><c:out value="${empty log.targetEmail ? '[Hàng loạt]' : log.targetEmail}"/></td>
                                    <td>
                                        ${log.reason}
                                        <c:if test="${not empty log.details}">
                                            <button class="btn btn-sm btn-outline" style="margin-left: 10px; padding: 2px 8px; font-size: 12px;" onclick="viewLogDetails('${fn:escapeXml(log.details)}')">👁️ Xem chi tiết</button>
                                        </c:if>
                                    </td>
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
                    <label>Họ tên đầy đủ</label>
                    <input type="text" class="form-control" id="editStuName">
                </div>
                <div class="form-group">
                    <label>CCCD</label>
                    <input type="text" class="form-control" id="editStuCccd">
                </div>
                <div class="form-group">
                    <label>Tên</label>
                    <input type="text" class="form-control" id="editStuFirstName">
                </div>
                <div class="form-group">
                    <label>Họ đệm</label>
                    <input type="text" class="form-control" id="editStuLastName">
                </div>
                <div class="form-group">
                    <label>Niên khóa</label>
                    <input type="text" class="form-control" id="editStuCohort">
                </div>
                <div class="form-group">
                    <label>Số điện thoại</label>
                    <input type="text" class="form-control" id="editStuPhone">
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
                    <label>Họ tên đầy đủ</label>
                    <input type="text" class="form-control" id="newStuName" placeholder="Nguyễn Văn A">
                </div>
                <div class="form-group">
                    <label>CCCD</label>
                    <input type="text" class="form-control" id="newStuCccd" placeholder="012345678901">
                </div>
                <div class="form-group">
                    <label>Tên</label>
                    <input type="text" class="form-control" id="newStuFirstName" placeholder="A">
                </div>
                <div class="form-group">
                    <label>Họ đệm</label>
                    <input type="text" class="form-control" id="newStuLastName" placeholder="Nguyễn Văn">
                </div>
                <div class="form-group">
                    <label>Niên khóa</label>
                    <input type="text" class="form-control" id="newStuCohort" placeholder="Ví dụ: 2020-2026">
                </div>
                <div class="form-group">
                    <label>Số điện thoại</label>
                    <input type="text" class="form-control" id="newStuPhone" placeholder="0987654321">
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-outline" onclick="closeCreateModal()">Hủy bỏ</button>
                <button class="btn btn-primary" onclick="submitCreate()">💾 Tạo sinh viên</button>
            </div>
        </div>
    </div>


    <!-- ================= MODAL XUẤT EXCEL ================= -->
    <div class="modal-overlay" id="exportModal">
        <div class="modal" style="max-width: 500px;">
            <div class="modal-header">
                <div class="modal-title">📥 Tùy chọn Xuất Excel</div>
                <button class="modal-close" onclick="closeExportModal()">✕</button>
            </div>
            <div class="modal-body">
                <form action="export-excel" method="GET" id="exportExcelForm">
                    <div class="form-group">
                        <label>Chọn trạng thái tài khoản cần xuất:</label>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-top: 10px;">
                            <label style="display: flex; align-items: center; gap: 8px; font-weight: normal; cursor: pointer;">
                                <input type="checkbox" name="status" value="1" checked> Hoạt động
                            </label>
                            <label style="display: flex; align-items: center; gap: 8px; font-weight: normal; cursor: pointer;">
                                <input type="checkbox" name="status" value="0"> Chờ kích hoạt
                            </label>
                            <label style="display: flex; align-items: center; gap: 8px; font-weight: normal; cursor: pointer;">
                                <input type="checkbox" name="status" value="2"> Đang bảo lưu
                            </label>
                            <label style="display: flex; align-items: center; gap: 8px; font-weight: normal; cursor: pointer;">
                                <input type="checkbox" name="status" value="3"> Chờ xóa
                            </label>
                        </div>
                    </div>
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
                        <div class="form-group">
                            <label>Khoa / Đơn vị</label>
                            <input type="text" name="department" class="form-control" placeholder="Ví dụ: Khoa Y...">
                        </div>
                        <div class="form-group">
                            <label>Ngành học</label>
                            <input type="text" name="major" class="form-control" placeholder="Ví dụ: Y đa khoa...">
                        </div>
                    </div>
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
                        <div class="form-group">
                            <label>Lớp</label>
                            <input type="text" name="studentClass" class="form-control" placeholder="Tên lớp...">
                        </div>
                        <div class="form-group">
                            <label>Khóa</label>
                            <input type="text" name="cohort" class="form-control" placeholder="2020...">
                        </div>
                    </div>
                    <div class="modal-footer" style="margin-top: 20px;">
                        <button type="button" class="btn btn-outline" onclick="closeExportModal()">Hủy</button>
                        <button type="submit" class="btn btn-primary" onclick="closeExportModal()">Xác nhận Xuất</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- LOG DETAILS MODAL -->
    <div class="modal-overlay" id="logDetailsModal">
        <div class="modal-content" style="max-width: 600px;">
            <div class="modal-header">
                <h2>Chi tiết Hoạt động</h2>
                <button class="close-btn" onclick="closeLogDetailsModal()">✕</button>
            </div>
            <div style="padding: 20px; max-height: 400px; overflow-y: auto;">
                <div id="logDetailsContent" style="background: var(--surface2); padding: 15px; border-radius: 8px; font-family: 'JetBrains Mono', monospace; font-size: 13px; color: var(--text); white-space: pre-wrap; word-break: break-all;"></div>
            </div>
            <div class="modal-footer" style="padding: 15px 20px; border-top: 1px solid var(--border);">
                <button type="button" class="btn btn-primary" onclick="closeLogDetailsModal()">Đóng</button>
            </div>
        </div>
    </div>

    <!-- TOAST CONTAINER -->
    <div class="toast-container" id="toastContainer"></div>

    <!-- jsPDF and AutoTable libraries -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.28/jspdf.plugin.autotable.min.js"></script>
    
    <script>
        function removeVietnameseTones(str) {
            str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g,"a"); 
            str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g,"e"); 
            str = str.replace(/ì|í|ị|ỉ|ĩ/g,"i"); 
            str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g,"o"); 
            str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g,"u"); 
            str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g,"y"); 
            str = str.replace(/đ/g,"d");
            str = str.replace(/À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ/g, "A");
            str = str.replace(/È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ/g, "E");
            str = str.replace(/Ì|Í|Ị|Ỉ|Ĩ/g, "I");
            str = str.replace(/Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ/g, "O");
            str = str.replace(/Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ/g, "U");
            str = str.replace(/Ỳ|Ý|Ỵ|Ỷ|Ỹ/g, "Y");
            str = str.replace(/Đ/g, "D");
            return str;
        }

        function exportRevokedToPDF() {
            try {
                const { jsPDF } = window.jspdf;
                const doc = new jsPDF();
                
                // Add title
                doc.setFontSize(18);
                doc.text("DANH SACH SINH VIEN CHO THU HOI TAI KHOAN", 105, 15, null, null, "center");
                
                const today = new Date();
                const dateStr = today.getDate() + "/" + (today.getMonth()+1) + "/" + today.getFullYear();
                doc.setFontSize(11);
                doc.text("Ngay ket xuat: " + dateStr, 105, 22, null, null, "center");

                // Prepare table data
                const table = document.getElementById("revokeTable");
                if(!table || table.rows.length <= 1 || (table.rows.length === 2 && table.rows[1].cells.length === 1)) {
                    alert("Không có dữ liệu để xuất PDF!");
                    return;
                }

                const data = [];
                for (let i = 1; i < table.rows.length; i++) {
                    const row = table.rows[i];
                    if (row.cells.length > 1) {
                        const email = removeVietnameseTones(row.cells[0].innerText.trim());
                        const maSV = removeVietnameseTones(row.cells[1].innerText.trim());
                        const hoTen = removeVietnameseTones(row.cells[2].innerText.trim());
                        const lop = removeVietnameseTones(row.cells[3].innerText.trim());
                        const ngayXoa = removeVietnameseTones(row.cells[5].innerText.trim());
                        data.push([i, hoTen, maSV, email, lop, ngayXoa]);
                    }
                }

                // Generate table
                doc.autoTable({
                    startY: 30,
                    head: [['STT', 'Ho ten', 'Ma SV', 'Email', 'Lop', 'Ngay du kien xoa']],
                    body: data,
                    theme: 'grid',
                    headStyles: { fillColor: [231, 76, 60] },
                    styles: { fontSize: 10, cellPadding: 3 }
                });

                // Save PDF
                const fileName = "Danh_Sach_Cho_Thu_Hoi_" + dateStr.replace(/\//g, "_") + ".pdf";
                doc.save(fileName);
                
                showToast("Đã xuất PDF thành công!", "success");
            } catch (err) {
                console.error(err);
                alert("Lỗi xuất PDF: " + err.message);
            }
        }

        function exportSuspendedToPDF() {
            try {
                const { jsPDF } = window.jspdf;
                const doc = new jsPDF();
                
                // Add title
                doc.setFontSize(18);
                doc.text("DANH SACH SINH VIEN BAO LUU TAI KHOAN", 105, 15, null, null, "center");
                
                const today = new Date();
                const dateStr = today.getDate() + "/" + (today.getMonth()+1) + "/" + today.getFullYear();
                doc.setFontSize(11);
                doc.text("Ngay ket xuat: " + dateStr, 105, 22, null, null, "center");

                // Prepare table data
                const table = document.getElementById("suspendedTable");
                if(!table || table.rows.length <= 1 || (table.rows.length === 2 && table.rows[1].cells.length === 1)) {
                    alert("Không có dữ liệu để xuất PDF!");
                    return;
                }

                const data = [];
                for (let i = 1; i < table.rows.length; i++) {
                    const row = table.rows[i];
                    if (row.cells.length > 1) {
                        const email = removeVietnameseTones(row.cells[0].innerText.trim());
                        const maSV = removeVietnameseTones(row.cells[1].innerText.trim());
                        const hoTen = removeVietnameseTones(row.cells[2].innerText.trim());
                        const lop = removeVietnameseTones(row.cells[3].innerText.trim());
                        const ngay = removeVietnameseTones(row.cells[4].innerText.trim());
                        data.push([i, hoTen, maSV, email, lop, ngay]);
                    }
                }

                // Generate table
                doc.autoTable({
                    startY: 30,
                    head: [['STT', 'Ho ten', 'Ma SV', 'Email', 'Lop', 'Ngay bao luu']],
                    body: data,
                    theme: 'grid',
                    headStyles: { fillColor: [52, 152, 219] },
                    styles: { fontSize: 10, cellPadding: 3 }
                });

                // Save PDF
                const fileName = "Danh_Sach_Bao_Luu_" + dateStr.replace(/\//g, "_") + ".pdf";
                doc.save(fileName);
                
                showToast("Đã xuất PDF thành công!", "success");
            } catch (err) {
                console.error(err);
                alert("Lỗi xuất PDF: " + err.message);
            }
        }
    </script>

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

    <script src="${pageContext.request.contextPath}/js/main_v5.js?v=5"></script>
</body>
</html>