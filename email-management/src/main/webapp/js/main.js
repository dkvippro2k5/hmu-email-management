/* =========================================================
   1. ĐIỀU HƯỚNG MODULE (SINGLE PAGE APPLICATION)
========================================================= */
const moduleTitles = {
    dashboard: 'Bảng điều khiển <span>/ Tổng quan hệ thống</span>',
    list: 'Danh sách tài khoản <span>/ Quản lý email</span>',
    revoke: 'Thu hồi tài khoản <span>/ Xử lý thu hồi</span>',
    archive: 'Bảo lưu tài khoản <span>/ Quản lý bảo lưu</span>',
    import: 'Import danh sách <span>/ Quản lý tệp dữ liệu</span>',
    log: 'Nhật ký hoạt động <span>/ Log hệ thống</span>',
    support: 'Support <span>/ Yêu cầu hỗ trợ</span>',
    notify: 'Gửi thông báo nâng cao <span>/ Nhắn tin hàng loạt</span>'
};

function showModule(modName, element) {
    document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
    if(element) element.classList.add('active');
    
    document.querySelectorAll('.page').forEach(page => page.classList.remove('active'));
    document.getElementById('page-' + modName).classList.add('active');
    
    document.getElementById('pageTitle').innerHTML = moduleTitles[modName] || modName;
}

/* =========================================================
   2. HỆ THỐNG THÔNG BÁO (TOAST NOTIFICATION)
========================================================= */
function showToast(msg, type = 'success') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = 'toast ' + type;
    toast.innerHTML = '<span class="toast-msg">' + msg + '</span>';
    container.appendChild(toast);
    
    // Auto remove after 3.5 seconds
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }, 3500);
}

// Tự động kiểm tra message từ server (redirect sau form import)
window.onload = function() {
    const sessionMsgDiv = document.getElementById('sessionMessage');
    if(sessionMsgDiv) {
        showToast(sessionMsgDiv.getAttribute('data-msg'), 'success');
    }
    const sessionErrorDiv = document.getElementById('sessionErrorMessage');
    if(sessionErrorDiv) {
        showToast(sessionErrorDiv.getAttribute('data-msg'), 'error');
    }

    const resetButton = document.getElementById('btnResetAll');
    if (resetButton) {
        resetButton.addEventListener('click', openResetModal);
    }

    // Logic hash được chuyển sang inline script ở file dashboard.jsp để tránh giật màn hình
};

/* =========================================================
   3. API ĐỒNG BỘ SSO
========================================================= */
function syncSSOData() {
    const btn = document.getElementById('btnSyncSSO');
    const spinner = document.getElementById('syncSpinner');
    const text = document.getElementById('syncText');

    // Bật hiệu ứng Loading ngay trên nút bấm
    btn.style.opacity = '0.8';
    btn.style.pointerEvents = 'none';
    spinner.style.display = 'inline-block';
    text.innerText = 'Đang đồng bộ...';

    fetch(`${contextPath}/sync-sso`, { method: 'POST' })
    .then(response => response.json())
    .then(data => {
        // Tắt hiệu ứng
        btn.style.opacity = '1';
        btn.style.pointerEvents = 'auto';
        spinner.style.display = 'none';
        text.innerText = '🔄 Đồng bộ SSO';

        if(data.status === "success") {
            showToast(data.message, 'success');
        } else {
            showToast("Lỗi: " + data.message, 'error');
        }
    })
    .catch(error => {
        btn.style.opacity = '1';
        btn.style.pointerEvents = 'auto';
        spinner.style.display = 'none';
        text.innerText = '🔄 Đồng bộ SSO';
        showToast("Lỗi kết nối máy chủ!", 'error');
    });
}

/* =========================================================
   4. IMPORT EXCEL TỰ ĐỘNG SUBMIT
========================================================= */
function confirmAndImport(event) {
    const fileInput = event.target;
    if(fileInput.files.length > 0) {
        const fileName = fileInput.files[0].name;
        if(confirm("Bạn có chắc chắn muốn import dữ liệu từ file: " + fileName + " không?")) {
            showToast("Đang tải dữ liệu lên hệ thống...", "success");
            document.getElementById('autoImportForm').submit();
        } else {
            fileInput.value = ""; // Xóa lựa chọn nếu hủy
        }
    }
}

function openResetModal() {
    document.getElementById('resetModal').classList.add('open');
}

function closeResetModal() {
    document.getElementById('resetModal').classList.remove('open');
}

function confirmReset(event) {
    if (!confirm('Bạn có chắc chắn muốn reset toàn bộ dữ liệu sinh viên? Hành động này sẽ xóa tất cả dữ liệu hiện tại và không thể hoàn tác.')) {
        event.preventDefault();
        return false;
    }
    return true;
}

/* =========================================================
   5. TÌM KIẾM SINH VIÊN (REAL-TIME + FILTER PANEL)
========================================================= */
let searchTimeout;

let suspendedSearchTimeout;
let revokeSearchTimeout;

function toggleFilterPanel() {
    const panel = document.getElementById('filterPanel');
    panel.classList.toggle('open');
}

function toggleSuspendedFilterPanel() {
    const panel = document.getElementById('suspendedFilterPanel');
    panel.classList.toggle('open');
}

function toggleRevokeFilterPanel() {
    const panel = document.getElementById('revokeFilterPanel');
    panel.classList.toggle('open');
}

// Đóng panel khi click ra ngoài
document.addEventListener('click', function(e) {
    const panel = document.getElementById('filterPanel');
    const toggleBtn = document.getElementById('btnFilterToggle');
    if (panel && panel.classList.contains('open')) {
        if (!panel.contains(e.target) && !toggleBtn.contains(e.target)) {
            panel.classList.remove('open');
        }
    }
    
    const panelS = document.getElementById('suspendedFilterPanel');
    const toggleBtnS = document.getElementById('btnSuspendedFilterToggle');
    if (panelS && panelS.classList.contains('open')) {
        if (!panelS.contains(e.target) && !toggleBtnS.contains(e.target)) {
            panelS.classList.remove('open');
        }
    }

    const panelR = document.getElementById('revokeFilterPanel');
    const toggleBtnR = document.getElementById('btnRevokeFilterToggle');
    if (panelR && panelR.classList.contains('open')) {
        if (!panelR.contains(e.target) && !toggleBtnR.contains(e.target)) {
            panelR.classList.remove('open');
        }
    }
});

function debounceRealtimeSearch() {
    const loadingIcon = document.getElementById('searchLoading');
    if (loadingIcon) loadingIcon.style.display = 'inline'; 

    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        triggerApiSearch();
    }, 400); 
}

function debounceSuspendedSearch() {
    const loadingIcon = document.getElementById('suspendedSearchLoading');
    if (loadingIcon) loadingIcon.style.display = 'inline'; 

    clearTimeout(suspendedSearchTimeout);
    suspendedSearchTimeout = setTimeout(() => {
        triggerSuspendedApiSearch();
    }, 400); 
}

function debounceRevokeSearch() {
    const loadingIcon = document.getElementById('revokeSearchLoading');
    if (loadingIcon) loadingIcon.style.display = 'inline'; 

    clearTimeout(revokeSearchTimeout);
    revokeSearchTimeout = setTimeout(() => {
        triggerRevokeApiSearch();
    }, 400); 
}

function applyFilters() {
    updateFilterBadge();
    triggerApiSearch();
    toggleFilterPanel(); 
}

function applySuspendedFilters() {
    updateSuspendedFilterBadge();
    triggerSuspendedApiSearch();
    toggleSuspendedFilterPanel(); 
}

function applyRevokeFilters() {
    updateRevokeFilterBadge();
    triggerRevokeApiSearch();
    toggleRevokeFilterPanel(); 
}

function clearFilters() {
    document.getElementById('statusFilter').value = "-1";
    document.getElementById('classFilter').value = "";
    document.getElementById('deptFilter').value = "";
    document.getElementById('majorFilter').value = "";
    document.getElementById('cohortFilter').value = "";
    updateFilterBadge();
    triggerApiSearch();
}

function resetSuspendedFilters() {
    document.getElementById('suspendedFilterClass').value = "";
    document.getElementById('suspendedFilterDept').value = "";
    document.getElementById('suspendedFilterMajor').value = "";
    document.getElementById('suspendedFilterCohort').value = "";
    updateSuspendedFilterBadge();
    triggerSuspendedApiSearch();
}

function resetRevokeFilters() {
    document.getElementById('revokeFilterClass').value = "";
    document.getElementById('revokeFilterDept').value = "";
    document.getElementById('revokeFilterMajor').value = "";
    document.getElementById('revokeFilterCohort').value = "";
    updateRevokeFilterBadge();
    triggerRevokeApiSearch();
}

function updateFilterBadge() {
    let count = 0;
    if (document.getElementById('statusFilter').value !== "-1") count++;
    if (document.getElementById('classFilter').value.trim() !== "") count++;
    if (document.getElementById('deptFilter').value.trim() !== "") count++;
    if (document.getElementById('majorFilter').value.trim() !== "") count++;
    if (document.getElementById('cohortFilter').value.trim() !== "") count++;

    const badge = document.getElementById('filterCountBadge');
    if (badge) {
        if (count > 0) {
            badge.innerText = count;
            badge.style.display = 'inline-block';
        } else {
            badge.style.display = 'none';
        }
    }
}

function updateSuspendedFilterBadge() {
    let count = 0;
    if (document.getElementById('suspendedFilterClass').value.trim() !== "") count++;
    if (document.getElementById('suspendedFilterDept').value.trim() !== "") count++;
    if (document.getElementById('suspendedFilterMajor').value.trim() !== "") count++;
    if (document.getElementById('suspendedFilterCohort').value.trim() !== "") count++;

    const badge = document.getElementById('suspendedFilterCountBadge');
    if (badge) {
        if (count > 0) {
            badge.innerText = count;
            badge.style.display = 'inline-block';
        } else {
            badge.style.display = 'none';
        }
    }
}

function updateRevokeFilterBadge() {
    let count = 0;
    if (document.getElementById('revokeFilterClass').value.trim() !== "") count++;
    if (document.getElementById('revokeFilterDept').value.trim() !== "") count++;
    if (document.getElementById('revokeFilterMajor').value.trim() !== "") count++;
    if (document.getElementById('revokeFilterCohort').value.trim() !== "") count++;

    const badge = document.getElementById('revokeFilterCountBadge');
    if (badge) {
        if (count > 0) {
            badge.innerText = count;
            badge.style.display = 'inline-block';
        } else {
            badge.style.display = 'none';
        }
    }
}

function triggerApiSearch(queryStr) {
    // 1. Lấy giá trị tìm kiếm và các bộ lọc từ Panel
    let keyword = queryStr !== undefined ? queryStr : document.getElementById('searchInput').value;
    let status = document.getElementById('statusFilter').value;
    let className = document.getElementById('classFilter').value;
    let department = document.getElementById('deptFilter').value;
    let major = document.getElementById('majorFilter').value;
    let cohort = document.getElementById('cohortFilter').value;
    
    const loadingIcon = document.getElementById('searchLoading');
    if (loadingIcon) loadingIcon.style.display = 'inline';

    const params = new URLSearchParams({
        keyword: keyword.trim(),
        status: status,
        className: className.trim(),
        department: department.trim(),
        major: major.trim(),
        cohort: cohort.trim()
    });

    const apiUrl = `${contextPath}/search-accounts?${params.toString()}`;

    fetch(apiUrl)
    .then(response => {
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        return response.json();
    })
    .then(data => {
        if (loadingIcon) loadingIcon.style.display = 'none';
        let tableBody = document.getElementById('studentTableBody');
        let htmlContent = '';

        if (!data || data.length === 0) {
            htmlContent = `<tr><td colspan="13" style="text-align: center; color: var(--text3); padding: 30px;">🔍 Không tìm thấy kết quả nào phù hợp.</td></tr>`;
        } else {
            data.forEach((student, index) => {
                let statusBadge = '';
                
                let actionButtons = `<button class="btn btn-outline btn-sm" title="Sửa" onclick="openEditModal(this)" ` +
                                    `data-id="${escapeHtml(student.studentId)}" ` +
                                    `data-name="${escapeHtml(student.studentName)}" ` +
                                    `data-gender="${escapeHtml(student.gender)}" ` +
                                    `data-dob="${escapeHtml(student.dateOfBirth)}" ` +
                                    `data-class="${escapeHtml(student.className)}" ` +
                                    `data-dept="${escapeHtml(student.department)}" ` +
                                    `data-major="${escapeHtml(student.major)}" ` +
                                    `data-cohort="${escapeHtml(student.cohort)}" ` +
                                    `data-email="${escapeHtml(student.emailAddress)}" ` +
                                    `data-personal="${escapeHtml(student.personalEmail)}" ` +
                                    `data-status="${student.status}">✏️</button>`;

                if (student.status === 0) {
                    statusBadge = '<span class="badge stt-pending"><span class="badge-dot"></span>Chờ kích hoạt</span>';
                    actionButtons += `<button class="btn btn-warning btn-sm" title="Khóa" onclick="suspendAccount('${student.studentId}')">🔒</button>`;
                } 
                else if (student.status === 1) {
                    statusBadge = '<span class="badge stt-active"><span class="badge-dot"></span>Hoạt động</span>';
                    actionButtons += `<button class="btn btn-warning btn-sm" title="Khóa" onclick="suspendAccount('${student.studentId}')">🔒</button>`;
                } 
                else if (student.status === 2) {
                    statusBadge = '<span class="badge stt-suspended"><span class="badge-dot"></span>Đã bảo lưu</span>';
                    actionButtons += `<button class="btn btn-success btn-sm" title="Khôi phục" onclick="restoreAccount('${student.studentId}')">↩️</button>`;
                } 
                else if (student.status === 3) {
                    statusBadge = '<span class="badge stt-revoking"><span class="badge-dot"></span>Chờ xóa</span>';
                    actionButtons += `<button class="btn btn-success btn-sm" title="Hủy xóa" onclick="restoreAccount('${student.studentId}')">↩️</button>`;
                }

                actionButtons += `<button class="btn btn-danger btn-sm" title="Xóa" onclick="deleteAccount('${student.studentId}')">🗑️</button>`;

                htmlContent += `
                    <tr>
                        <td class="mono">${index + 1}</td>
                        <td class="td-main">${escapeHtml(student.studentName || '---')}</td>
                        <td class="mono">${escapeHtml(student.studentId || '---')}</td>
                        <td>${escapeHtml(student.gender || '---')}</td>
                        <td class="mono">${escapeHtml(student.dateOfBirth || '---')}</td>
                        <td>${escapeHtml(student.className || '---')}</td>
                        <td>${escapeHtml(student.department || '---')}</td>
                        <td>${escapeHtml(student.major || '---')}</td>
                        <td class="mono">${escapeHtml(student.cohort || '---')}</td>
                        <td class="mono" style="font-size:12px;">${escapeHtml(student.personalEmail || '---')}</td>
                        <td class="mono" style="color:var(--accent2); font-weight:600;">${escapeHtml(student.emailAddress || '---')}</td>
                        <td>${statusBadge}</td>
                        <td>
                            <div style="display:flex; gap:6px; justify-content: flex-end;">
                                ${actionButtons}
                            </div>
                        </td>
                    </tr>
                `;
            });
        }
        
        tableBody.innerHTML = htmlContent;
    })
    .catch(error => {
        if (loadingIcon) loadingIcon.style.display = 'none';
        console.error('Lỗi khi tìm kiếm sinh viên:', error);
        showToast("Lỗi khi tải dữ liệu. Vui lòng kiểm tra kết nối!", "error");
        document.getElementById('studentTableBody').innerHTML = `<tr><td colspan="13" style="text-align: center; color: var(--red); padding: 20px;">⚠️ Không thể kết nối đến máy chủ.</td></tr>`;
    });
}

function triggerSuspendedApiSearch(queryStr) {
    let keyword = queryStr !== undefined ? queryStr : document.getElementById('suspendedSearchInput').value;
    let className = document.getElementById('suspendedFilterClass').value;
    let department = document.getElementById('suspendedFilterDept').value;
    let major = document.getElementById('suspendedFilterMajor').value;
    let cohort = document.getElementById('suspendedFilterCohort').value;
    
    const loadingIcon = document.getElementById('suspendedSearchLoading');
    if (loadingIcon) loadingIcon.style.display = 'inline';

    const params = new URLSearchParams({
        keyword: keyword.trim(),
        status: 2, // Hardcode status = 2 for suspended
        className: className.trim(),
        department: department.trim(),
        major: major.trim(),
        cohort: cohort.trim()
    });

    const apiUrl = `${contextPath}/search-accounts?${params.toString()}`;

    fetch(apiUrl)
    .then(response => {
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        return response.json();
    })
    .then(data => {
        if (loadingIcon) loadingIcon.style.display = 'none';
        let tableBody = document.getElementById('suspendedTbody');
        let htmlContent = '';

        if (!data || data.length === 0) {
            htmlContent = `<tr><td colspan="6" style="text-align: center; color: var(--text3); padding: 30px;">🔍 Không tìm thấy kết quả nào phù hợp.</td></tr>`;
        } else {
            data.forEach((student, index) => {
                let activationDateStr = '';
                if (student.activationDate) {
                    const dateObj = new Date(student.activationDate);
                    activationDateStr = String(dateObj.getDate()).padStart(2, '0') + '/' + String(dateObj.getMonth() + 1).padStart(2, '0') + '/' + dateObj.getFullYear();
                }
                htmlContent += `
                    <tr>
                        <td><div style="font-weight: 500;">${escapeHtml(student.emailAddress)}</div></td>
                        <td style="color: var(--text2);">${escapeHtml(student.studentId)}</td>
                        <td>${escapeHtml(student.studentName)}</td>
                        <td>${escapeHtml(student.className)}</td>
                        <td>${activationDateStr}</td>
                        <td><span class="badge" style="background: rgba(231, 76, 60, 0.15); color: #e74c3c;">Đã bảo lưu</span></td>
                    </tr>
                `;
            });
        }
        
        tableBody.innerHTML = htmlContent;
    })
    .catch(error => {
        if (loadingIcon) loadingIcon.style.display = 'none';
        showToast("Lỗi khi tải dữ liệu. Vui lòng kiểm tra kết nối!", "error");
    });
}

function triggerRevokeApiSearch(queryStr) {
    let keyword = queryStr !== undefined ? queryStr : document.getElementById('revokeSearchInput').value;
    let className = document.getElementById('revokeFilterClass').value;
    let department = document.getElementById('revokeFilterDept').value;
    let major = document.getElementById('revokeFilterMajor').value;
    let cohort = document.getElementById('revokeFilterCohort').value;
    
    const loadingIcon = document.getElementById('revokeSearchLoading');
    if (loadingIcon) loadingIcon.style.display = 'inline';

    const params = new URLSearchParams({
        keyword: keyword.trim(),
        status: 3, // Hardcode status = 3 for waiting revoke
        className: className.trim(),
        department: department.trim(),
        major: major.trim(),
        cohort: cohort.trim()
    });

    const apiUrl = `${contextPath}/search-accounts?${params.toString()}`;

    fetch(apiUrl)
    .then(response => {
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        return response.json();
    })
    .then(data => {
        if (loadingIcon) loadingIcon.style.display = 'none';
        let tableBody = document.getElementById('revokeTbody');
        let htmlContent = '';

        if (!data || data.length === 0) {
            htmlContent = `<tr><td colspan="8" style="text-align: center; color: var(--text3); padding: 30px;">🔍 Không tìm thấy kết quả nào phù hợp.</td></tr>`;
        } else {
            data.forEach((student, index) => {
                let activationDateStr = '';
                if (student.activationDate) {
                    const dateObj = new Date(student.activationDate);
                    activationDateStr = String(dateObj.getDate()).padStart(2, '0') + '/' + String(dateObj.getMonth() + 1).padStart(2, '0') + '/' + dateObj.getFullYear();
                }
                
                let deleteDateStr = '<span style="color: var(--text3);">Chưa lên lịch</span>';
                if (student.scheduledDeleteDate) {
                    const dDate = new Date(student.scheduledDeleteDate);
                    const formattedDDate = String(dDate.getDate()).padStart(2, '0') + '/' + String(dDate.getMonth() + 1).padStart(2, '0') + '/' + dDate.getFullYear();
                    deleteDateStr = `<span style="color: #d97706; font-weight: 500; display: inline-flex; align-items: center; gap: 4px;">
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z"/><path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z"/></svg>
                        ${formattedDDate}
                    </span>`;
                }

                htmlContent += `
                    <tr>
                        <td><div style="font-weight: 500;">${escapeHtml(student.emailAddress)}</div></td>
                        <td style="color: var(--text2);">${escapeHtml(student.studentId)}</td>
                        <td>${escapeHtml(student.studentName)}</td>
                        <td>${escapeHtml(student.className)}</td>
                        <td>${activationDateStr}</td>
                        <td>${deleteDateStr}</td>
                        <td><span class="badge" style="background: rgba(245, 158, 11, 0.1); color: #d97706; border-color: rgba(245, 158, 11, 0.2);">Chờ xóa</span></td>
                        <td style="text-align: right;">
                            <button class="action-btn" title="Hủy xóa (Khôi phục)" onclick="restoreAccount('${student.emailAddress}')" style="color: #059669; background: rgba(16, 185, 129, 0.1);">
                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16"><path d="M9.302 1.256a1.5 1.5 0 0 0-2.604 0l-1.704 2.98a.5.5 0 0 0 .869.497l1.703-2.981a.5.5 0 0 1 .868 0l2.54 4.444-1.256-.337a.5.5 0 1 0-.26.966l2.415.647a.5.5 0 0 0 .613-.353l.647-2.415a.5.5 0 1 0-.966-.259l-.333 1.242-2.532-4.431zM2.973 7.773l-1.255.337a.5.5 0 1 1-.26-.966l2.416-.647a.5.5 0 0 1 .612.353l.647 2.415a.5.5 0 0 1-.966.259l-.333-1.242-2.545 4.454a.5.5 0 0 0 .434.748H5a.5.5 0 0 1 0 1H1.723A1.5 1.5 0 0 1 .421 12.24l2.552-4.467zm10.89 1.463a.5.5 0 1 0-.868.496l1.716 3.004a.5.5 0 0 1-.434.748h-2.56l.34-.85a.5.5 0 1 0-.928-.372l-.636 1.58a.5.5 0 0 0 .373.636l1.58.636a.5.5 0 0 0 .372-.928l-.855-.34h2.56a1.5 1.5 0 0 0 1.302-2.244l-1.716-3.004z"/></svg>
                            </button>
                        </td>
                    </tr>
                `;
            });
        }
        
        tableBody.innerHTML = htmlContent;
    })
    .catch(error => {
        if (loadingIcon) loadingIcon.style.display = 'none';
        showToast("Lỗi khi tải dữ liệu. Vui lòng kiểm tra kết nối!", "error");
    });
}

/* =========================================================
   6. CÁC THAO TÁC API: KHÓA / XÓA / SỬA TÀI KHOẢN
========================================================= */
function suspendAccount(studentId) {
    let decisionNumber = window.prompt(`[BẢO LƯU TÀI KHOẢN]\nVui lòng nhập Số hiệu Quyết định bảo lưu cho sinh viên ${studentId}:`, "VD: 125/QĐ-HMU");
    
    if (decisionNumber === null || decisionNumber.trim() === "") {
        showToast("Bắt buộc phải nhập Số hiệu Quyết định để lưu hồ sơ!", "error");
        return;
    }

    let formData = new URLSearchParams();
    formData.append("studentId", studentId);
    formData.append("decisionNumber", decisionNumber.trim());

    fetch(`${contextPath}/suspend-account`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
    .then(response => response.json())
    .then(data => {
        if (data.success || data.status === "success") {
            showToast("Thành công: " + data.message, "success");
            triggerApiSearch(); // Cập nhật lại bảng
        } else {
            showToast("Lỗi: " + data.message, "error");
        }
    })
    .catch(error => {
        showToast("Lỗi hệ thống khi kết nối tới máy chủ!", "error");
    });
}

function restoreAccount(studentId) {
    if (confirm('Bạn có chắc chắn muốn khôi phục tài khoản: ' + studentId + '?')) {
        let formData = new URLSearchParams();
        formData.append('studentId', studentId);

        fetch(`${contextPath}/restore-account`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        })
        .then(response => response.json())
        .then(data => {
            if (data.success || data.status === "success") {
                showToast(data.message, 'success');
                triggerApiSearch();
                triggerSuspendedApiSearch();
                triggerRevokeApiSearch();
            } else {
                showToast('Lỗi: ' + data.message, 'error');
            }
        })
        .catch(() => showToast('Lỗi khi kết nối đến máy chủ!', 'error'));
    }
}

function deleteAccount(studentId) {
    if (confirm('CẢNH BÁO: Hành động này sẽ đưa tài khoản ' + studentId + ' vào trạng thái chờ xóa. Bạn có tiếp tục?')) {
        let formData = new URLSearchParams();
        formData.append('studentId', studentId);

        fetch(`${contextPath}/delete-account`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        })
        .then(response => response.json())
        .then(data => {
            if (data.success || data.status === "success") {
                showToast(data.message, 'success');
                triggerApiSearch();
                triggerSuspendedApiSearch();
                triggerRevokeApiSearch();
            } else {
                showToast('Lỗi: ' + data.message, 'error');
            }
        })
        .catch(() => showToast('Lỗi khi kết nối đến máy chủ!', 'error'));
    }
}

/* =========================================================
   7. MODAL CHỈNH SỬA THÔNG TIN
========================================================= */
function openEditModal(button) {
    const editBtn = button;
    document.getElementById('editStuId').value = editBtn.dataset.id || '';
    document.getElementById('editStuName').value = editBtn.dataset.name || '';
    document.getElementById('editStuEmail').value = editBtn.dataset.email || '';
    document.getElementById('editStuGender').value = editBtn.dataset.gender || '';
    document.getElementById('editStuDob').value = editBtn.dataset.dob || '';
    document.getElementById('editStuClass').value = editBtn.dataset.class || '';
    document.getElementById('editStuDept').value = editBtn.dataset.dept || '';
    document.getElementById('editStuMajor').value = editBtn.dataset.major || '';
    document.getElementById('editStuCohort').value = editBtn.dataset.cohort || '';
    document.getElementById('editStuPersonalEmail').value = editBtn.dataset.personal || '';
    document.getElementById('editStuStatus').value = editBtn.dataset.status || '1';
    document.getElementById('editModal').classList.add('open');
}

function closeEditModal() {
    document.getElementById('editModal').classList.remove('open');
}

function submitEdit() {
    const id = document.getElementById('editStuId').value;
    const name = document.getElementById('editStuName').value;
    const email = document.getElementById('editStuEmail').value;
    const gender = document.getElementById('editStuGender').value;
    const dob = document.getElementById('editStuDob').value;
    const className = document.getElementById('editStuClass').value;
    const dept = document.getElementById('editStuDept').value;
    const major = document.getElementById('editStuMajor').value;
    const cohort = document.getElementById('editStuCohort').value;
    const personalEmail = document.getElementById('editStuPersonalEmail').value;
    const status = document.getElementById('editStuStatus').value;

    const formData = new URLSearchParams();
    formData.append('studentId', id);
    formData.append('fullName', name);
    formData.append('emailAddress', email);
    formData.append('gender', gender);
    formData.append('dateOfBirth', dob);
    formData.append('className', className);
    formData.append('department', dept);
    formData.append('major', major);
    formData.append('cohort', cohort);
    formData.append('personalEmail', personalEmail);
    formData.append('status', status);

    fetch(`${contextPath}/update-student`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast(data.message, 'success');
            closeEditModal();
            triggerApiSearch();
        } else {
            showToast('Lỗi: ' + data.message, 'error');
        }
    })
    .catch(() => showToast('Lỗi khi lưu thông tin!', 'error'));
}

function openCreateModal() {
    document.getElementById('createModal').classList.add('open');
}

function closeCreateModal() {
    document.getElementById('createModal').classList.remove('open');
}

function submitCreate() {
    const studentId = document.getElementById('newStuId').value.trim();
    const fullName = document.getElementById('newStuName').value.trim();
    const dateOfBirth = document.getElementById('newStuDob').value;
    const gender = document.getElementById('newStuGender').value;
    const className = document.getElementById('newStuClass').value.trim();
    const department = document.getElementById('newStuDept').value.trim();
    const major = document.getElementById('newStuMajor').value.trim();
    const cohort = document.getElementById('newStuCohort').value.trim();
    const personalEmail = document.getElementById('newStuPersonalEmail').value.trim();

    if (!studentId || !fullName) {
        showToast('Mã sinh viên và họ tên không được để trống.', 'error');
        return;
    }

    const formData = new URLSearchParams();
    formData.append('studentId', studentId);
    formData.append('fullName', fullName);
    formData.append('dateOfBirth', dateOfBirth);
    formData.append('gender', gender);
    formData.append('className', className);
    formData.append('department', department);
    formData.append('major', major);
    formData.append('cohort', cohort);
    formData.append('personalEmail', personalEmail);

    fetch(`${contextPath}/create-student`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast(data.message, 'success');
            closeCreateModal();
            triggerApiSearch();
        } else {
            showToast('Lỗi: ' + data.message, 'error');
        }
    })
    .catch(() => showToast('Lỗi khi tạo sinh viên!', 'error'));
}

function escapeHtml(value) {
    if (value === null || value === undefined) return '';
    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// Đóng Modal khi click ra ngoài vùng xám (overlay)
const editModal = document.getElementById('editModal');
const createModal = document.getElementById('createModal');
const resetModal = document.getElementById('resetModal');

if (editModal) {
    editModal.addEventListener('click', function(e) {
        if (e.target === this) closeEditModal();
    });
}

if (createModal) {
    createModal.addEventListener('click', function(e) {
        if (e.target === this) closeCreateModal();
    });
}

if (resetModal) {
    resetModal.addEventListener('click', function(e) {
        if (e.target === this) closeResetModal();
    });
}

/* =========================================================
   8. TEST AUTO ACTIVATION (FOR DEVELOPMENT)
========================================================= */
function testAutoActivation() {
    if(confirm('Bạn có muốn chạy thử nghiệm kích hoạt tự động tài khoản ngay bây giờ?')) {
        showToast('Đang kiểm tra và kích hoạt tài khoản...', 'success');

        fetch(`${contextPath}/test-auto-activation`, {
            method: 'GET'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast(data.message, 'success');
                // Reload dashboard để cập nhật thống kê
                setTimeout(() => {
                    if (document.querySelector('.nav-item[onclick*="dashboard"]').classList.contains('active')) {
                        location.reload();
                    }
                }, 1000);
            } else {
                showToast('Lỗi: ' + data.message, 'error');
            }
        })
        .catch(error => {
            showToast('Lỗi kết nối đến máy chủ!', 'error');
        });
    }
}
function openExportModal() { document.getElementById('exportModal').classList.add('open'); }
function closeExportModal() { document.getElementById('exportModal').classList.remove('open'); }
