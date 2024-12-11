package com.example.khuvote.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Component
public class HashUtil {
    public String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedHash = digest.digest(input.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("문제가 발생했습니다.");
        }
    }
}
