package org.mss301.paymentservice.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
public class OnepaySignatureUtil {

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String hmacSHA256(String hexKey, String data) {
        try {
            if (hexKey == null || data == null) {
                throw new NullPointerException();
            }
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            byte[] keyBytes = hexToBytes(hexKey);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
            sha256_HMAC.init(secretKey);

            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = sha256_HMAC.doFinal(dataBytes);

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString().toUpperCase();
        } catch (Exception ex) {
            log.error("Lỗi khi tính HMAC SHA256 cho OnePay: {}", ex.getMessage());
            return "";
        }
    }
}
