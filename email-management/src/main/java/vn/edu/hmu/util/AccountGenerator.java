package vn.edu.hmu.util;

import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.Random;

public class AccountGenerator {

    // Hàm 1: Loại bỏ dấu tiếng Việt (Nguyễn Văn A -> Nguyen Van A)
    private static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace("đ", "d").replace("Đ", "D");
    }

    // Hàm 2: Sinh Email từ Họ tên và Mã SV
    public static String generateEmail(String fullName, String studentId) {
        String cleanName = removeAccent(fullName.trim().toLowerCase());
        String[] words = cleanName.split("\\s+");
        
        StringBuilder emailPrefix = new StringBuilder();
        // Lấy tên thật (từ cuối cùng)
        emailPrefix.append(words[words.length - 1]); 
        
        // Lấy các chữ cái đầu của Họ và Tên đệm
        for (int i = 0; i < words.length - 1; i++) {
            emailPrefix.append(words[i].charAt(0));
        }
        
        // Ghép với Mã SV và đuôi trường
        String localPart = emailPrefix.toString() + studentId;
        if (localPart.length() > 40) {
            localPart = localPart.substring(0, 40);
        }
        return localPart + "@hmu.edu.vn";
    }

    // Hàm 3: Sinh mật khẩu mặc định (Ví dụ: Hmu@ + chuỗi ngẫu nhiên 6 số)
    public static String generateDefaultPassword() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return "Hmu@" + String.format("%06d", number);
    }
}