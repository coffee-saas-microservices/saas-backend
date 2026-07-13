package org.mss301.paymentservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.paymentservice.config.OnepayProperties;
import org.mss301.paymentservice.utils.OnepaySignatureUtil;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnepayApiClient {

    private final OnepayProperties onepayProperties;

    public String createPaymentLink(Long orderId, String orderCode, Long amount, String description, String clientIp) {
        Map<String, String> vpcParams = new HashMap<>();
        vpcParams.put("vpc_Version", "2");
        vpcParams.put("vpc_Command", "pay");
        vpcParams.put("vpc_Merchant", onepayProperties.getMerchantId());
        vpcParams.put("vpc_AccessCode", onepayProperties.getAccessCode());
        vpcParams.put("vpc_MerchTxnRef", orderCode);
        vpcParams.put("vpc_OrderInfo", orderCode);
        vpcParams.put("vpc_Amount", String.valueOf(amount * 100));
        vpcParams.put("vpc_ReturnURL", onepayProperties.getReturnUrl());
        vpcParams.put("vpc_Locale", "vn");
        vpcParams.put("vpc_TicketNo", clientIp != null ? clientIp : "127.0.0.1");

        // Sort fields alphabetically
        List<String> fieldNames = new ArrayList<>(vpcParams.keySet());
        Collections.sort(fieldNames);

        List<String> queryParts = new ArrayList<>();
        List<String> hashParts = new ArrayList<>();
        for (String fieldName : fieldNames) {
            String fieldValue = vpcParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);
                hashParts.add(fieldName + "=" + fieldValue);
                queryParts.add(fieldName + "=" + encodedValue);
            }
        }

        String hashData = String.join("&", hashParts);
        String queryUrl = String.join("&", queryParts);

        log.info("[OnePay API] hashData: {}", hashData);

        String vpcSecureHash = OnepaySignatureUtil.hmacSHA256(onepayProperties.getHashSecret(), hashData);
        log.info("[OnePay API] vpcSecureHash: {}", vpcSecureHash);
        queryUrl += "&vpc_SecureHash=" + vpcSecureHash;

        return onepayProperties.getApiUrl() + "?" + queryUrl;
    }
}
