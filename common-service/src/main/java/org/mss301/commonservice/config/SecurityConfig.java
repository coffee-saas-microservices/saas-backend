package org.mss301.commonservice.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    private boolean isExcludedPath(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator")
                || path.startsWith("/api/system/")
                || path.startsWith("/api/system-admin/register");
    }
}
