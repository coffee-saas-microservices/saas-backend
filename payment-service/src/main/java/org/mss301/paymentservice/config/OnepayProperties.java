package org.mss301.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "onepay")
public class OnepayProperties {
    private String merchantId;
    private String accessCode;
    private String hashSecret;
    private String returnUrl;
    private String apiUrl;
}
