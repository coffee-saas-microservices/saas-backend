package org.mss301.shopservice.config;

import org.mss301.commonservice.multitenancy.CachingTenantResolver;
import org.mss301.commonservice.multitenancy.TenantFilter;
import org.mss301.commonservice.multitenancy.TenantResolver;
import org.mss301.shopservice.repository.ShopRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantConfig {

    @Bean
    public TenantResolver tenantResolver(ShopRepository shopRepository) {
        TenantResolver base = domain -> shopRepository.findByDomain(domain)
                .map(shop -> shop.getId())
                .orElse(null);
        return new CachingTenantResolver(base, 5 * 60 * 1000L); // cache 5 phút
    }

    @Bean
    public FilterRegistrationBean<TenantFilter> tenantFilter(TenantResolver tenantResolver) {
        TenantFilter filter = new TenantFilter(tenantResolver);
        FilterRegistrationBean<TenantFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
}
