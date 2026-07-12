package org.mss301.orderservice.config;

import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.multitenancy.CachingTenantResolver;
import org.mss301.commonservice.multitenancy.TenantFilter;
import org.mss301.commonservice.multitenancy.TenantResolver;
import org.mss301.orderservice.client.ShopServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Slf4j
@Configuration
public class TenantConfig {

    private static final long CACHE_TTL_MS = 5 * 60 * 1000L;

    @Bean
    public TenantResolver tenantResolver(ShopServiceClient shopServiceClient) {
        TenantResolver feignResolver = domain -> {
            try {
                ResponseEntity<Map<String, Object>> response = shopServiceClient.getShopByDomain(domain);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Object id = response.getBody().get("id");
                    return id != null ? ((Number) id).longValue() : null;
                }
            } catch (Exception e) {
                log.error("Lỗi khi gọi shop-service để resolve domain: {}", domain, e);
            }
            return null;
        };

        return new CachingTenantResolver(feignResolver, CACHE_TTL_MS);
    }

    @Bean
    public TenantFilter tenantFilter(TenantResolver tenantResolver) {
        return new TenantFilter(tenantResolver);
    }
}
