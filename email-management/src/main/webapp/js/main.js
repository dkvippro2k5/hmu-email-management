// Hàm gọi API đồng bộ SSO
function syncSSOData() {
    // Hiện chữ Loading xoay xoay cạnh nút bấm
    document.getElementById('apiLoading').style.display = 'inline-flex';

    // Dùng Fetch API gọi ngầm lên Servlet
    fetch('sync-sso', {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        // Tắt chữ Loading
        document.getElementById('apiLoading').style.display = 'none';

        // Kiểm tra kết quả và hiện Toast
        if(data.status === "success") {
            showToast(data.message);
        } else {
            alert("Lỗi: " + data.message);
        }
    })
    .catch(error => {
        document.getElementById('apiLoading').style.display = 'none';
        alert("Lỗi kết nối máy chủ!");
    });
}

// Hàm xử lý hiệu ứng trượt của Toast Notification
function showToast(message) {
    var toast = document.getElementById("toastNotification");
    document.getElementById("toastMessage").innerText = message;
    
    // Kích hoạt class 'show' để trượt vào
    toast.classList.add("show");

    // Sau 3.5 giây, gỡ class 'show' để trượt ra mất
    setTimeout(function(){ 
        toast.classList.remove("show"); 
    }, 3500);
}

// Hàm tìm kiếm sinh viên theo mã SV hoặc họ tên
function searchStudent() {
    // 1. Lấy từ khóa tìm kiếm từ ô input   
    let keyword = document.getElementById('searchInput').value.toLowerCase();

    // 2. Gọi API GET /search-accounts?keyword=... để lấy danh sách sinh viên phù hợp
    fetch('search-accounts?keyword=' + encodeURIComponent(keyword))
        .then(response => response.json())
        .then(data => {

            console.log("Dữ liệu thật từ Java gửi sang là: ", data[0]);

            // 3. Xóa hết các dòng hiện tại trong bảng
            let tableBody = document.getElementById('studentTableBody');
            let htmlContent = '';

            data.forEach((student, index) => {
                // Xác định màu sắc của nhãn trạng thái
                let statusBadge = '';
                if(student.status === 0) statusBadge = '<span class="status stt-pending">Chờ kích hoạt</span>';
                else if (student.status === 1) statusBadge = '<span class="status stt-active">Hoạt động</span>';
                else if (student.status === 2) statusBadge = '<span class="status stt-suspended">Đã bảo lưu</span>';
                else if (student.status === 3) statusBadge = '<span class="status stt-revorking">Chờ xóa</span>';

                // Vẽ lại các dòng mới (Đã BỎ dấu \ đi vì đây là file .js độc lập)
                htmlContent += `
                    <tr>
                        <td>${index + 1}</td>
                        <td>${student.studentId}</td>
                        <td>${student.studentName}</td>
                        <td>${student.emailAddress}</td>
                        <td>${student.activationDate ? student.activationDate : ''}</td>
                        <td>${statusBadge}</td>
                        <td class="action-links">
                            <a href="javascript:void(0)" 
                               onclick="suspendAccount('${student.studentId}')" 
                               style="${student.status == 2 ? 'color: gray; pointer-events: none;' : ''}">
                               ${student.status == 2 ? 'Đã khóa' : 'Khóa'}
                            </a>
                            <a href="javascript:void(0)" style="color: red;">Xóa</a>
                        </td>
                    </tr>
                `;
            });

            // Nếu không có kết quả nào, hiển thị dòng "Không tìm thấy sinh viên nào" (Đã đổi thành colspan="7")
            if(data.length === 0) {
                htmlContent = `<tr><td colspan="7" style="text-align: center; color: red;">Không tìm thấy sinh viên nào</td></tr>`;
            }

            tableBody.innerHTML = htmlContent;
        })
        .catch(error => {
            console.error('Lỗi khi tìm kiếm sinh viên:', error);
        });
}

// Hàm xử lý Khóa tài khoản (Bảo lưu)
function suspendAccount(studentId) {
    // 1. Hiển thị Popup yêu cầu nhập Số Quyết Định
    let decisionNumber = window.prompt(`[BẢO LƯU TÀI KHOẢN]\nVui lòng nhập Số hiệu Quyết định bảo lưu cho sinh viên ${studentId}:`, "VD: 125/QĐ-HMU");
    
    // 2. Kiểm tra nếu người dùng bấm Hủy (Cancel) hoặc để trống
    if (decisionNumber === null || decisionNumber.trim() === "") {
        alert("Thao tác bị hủy: Bắt buộc phải nhập Số hiệu Quyết định để lưu hồ sơ!");
        return; // Dừng lại, không chạy tiếp
    }

    // 3. Đóng gói dữ liệu để gửi đi
    let formData = new URLSearchParams();
    formData.append("studentId", studentId);
    formData.append("decisionNumber", decisionNumber.trim());

    // 4. Gọi API bằng Fetch POST
    fetch('suspend-account', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Nếu thành công: Báo cáo và gọi lại hàm tìm kiếm để bảng tự động load lại
            alert("✅ Thành công: " + data.message);
            searchStudent(); 
        } else {
            // Nếu thất bại (Lỗi Database, thiếu dữ liệu...)
            alert("❌ Lỗi: " + data.message);
        }
    })
    .catch(error => {
        console.error('Lỗi khi khóa tài khoản:', error);
        alert("❌ Đã xảy ra lỗi hệ thống khi kết nối tới máy chủ!");
    });
}