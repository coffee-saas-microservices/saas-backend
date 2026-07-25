package org.mss301.paymentservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.paymentservice.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/api/onepay", "/api/payments/onepay"})
@RequiredArgsConstructor
@Slf4j
public class OnepayIpnController {

    private final PaymentService paymentService;

    @GetMapping("/ipn")
    public String handleIpn(HttpServletRequest request) {
        log.info("[OnePay IPN] Nhận callback...");
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }
        return paymentService.processOnepayIpn(fields);
    }

    @GetMapping(value = "/result", produces = "text/html;charset=UTF-8")
    public String handleResult(HttpServletRequest request) {
        log.info("[OnePay Result] Nhận redirect...");
        String responseCode = request.getParameter("vpc_TxnResponseCode");

        // Gửi thông tin xử lý cập nhật trạng thái thanh toán (đề phòng IPN đến chậm)
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }
        try {
            paymentService.processOnepayIpn(fields);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý thanh toán OnePay trong redirect: {}", e.getMessage());
        }

        if ("0".equals(responseCode)) {
            return "<html>" +
                    "<head><meta charset='UTF-8'><title>Kết quả thanh toán</title></head>" +
                    "<body style='display:flex; justify-content:center; align-items:center; height:100vh; font-family:Arial, sans-serif; background-color:#f4f7f6; margin:0;'>" +
                    "  <div style='background:white; padding:40px; border-radius:12px; box-shadow:0 4px 15px rgba(0,0,0,0.1); text-align:center; max-width:400px;'>" +
                    "    <div style='font-size:72px; color:#2ecc71; margin-bottom:20px; font-weight:bold;'>SUCCESS</div>" +
                    "    <h2 style='color:#333; margin:0 0 10px 0;'>Thanh toán thành công!</h2>" +
                    "    <p style='color:#666; margin:0 0 20px 0;'>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";
        } else {
            return "<html>" +
                    "<head><meta charset='UTF-8'><title>Kết quả thanh toán</title></head>" +
                    "<body style='display:flex; justify-content:center; align-items:center; height:100vh; font-family:Arial, sans-serif; background-color:#f4f7f6; margin:0;'>" +
                    "  <div style='background:white; padding:40px; border-radius:12px; box-shadow:0 4px 15px rgba(0,0,0,0.1); text-align:center; max-width:400px;'>" +
                    "    <div style='font-size:72px; color:#e74c3c; margin-bottom:20px; font-weight:bold;'>FAIL</div>" +
                    "    <h2 style='color:#333; margin:0 0 10px 0;'>Thanh toán thất bại!</h2>" +
                    "    <p style='color:#666; margin:0 0 20px 0;'>Giao dịch không thành công hoặc đã bị hủy.</p>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";
        }
    }
}
