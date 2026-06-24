package vn.edu.hmu.util;

import javax.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileStorageUtil {
    
    // Thư mục mặc định lưu trữ file minh chứng
    private static final String UPLOAD_DIR = "C:/hmu_email_storage";

    /**
     * Lưu file upload vào server
     * @param filePart File được upload từ client
     * @param typePrefix Tiền tố loại file (VD: M01_KHOITAO, M02_BAOLUU)
     * @param adminId Tên hoặc ID admin thực hiện
     * @return Đường dẫn tuyệt đối tới file đã lưu
     */
    public static String saveUploadedFile(Part filePart, String typePrefix, String adminId) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Lấy tên file gốc
            String submittedFileName = filePart.getSubmittedFileName();
            if (submittedFileName == null || submittedFileName.trim().isEmpty()) {
                return null;
            }
            
            String extension = "";
            int extIndex = submittedFileName.lastIndexOf(".");
            if (extIndex > 0) {
                extension = submittedFileName.substring(extIndex);
            }

            // Tạo tên file mới có chứa timestamp và thông tin admin để dễ tra cứu kiểm toán
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = typePrefix + "_" + adminId + "_" + timestamp + extension;
            
            File targetFile = new File(UPLOAD_DIR + File.separator + fileName);
            
            // Lưu file
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            return targetFile.getAbsolutePath();
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
