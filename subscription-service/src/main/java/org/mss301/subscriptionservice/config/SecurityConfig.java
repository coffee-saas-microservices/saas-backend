package org.mss301.subscriptionservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cấu hình bảo mật cho subscription-service.
 *
 * GHI CHÚ (chế độ test local):
 * - Đang để permitAll toàn bộ để bạn test trực tiếp trên Swagger mà KHÔNG cần Keycloak.
 * - Khi triển khai thật theo chuẩn team (xác thực JWT qua Keycloak), bỏ comment khối
 *   oauth2ResourceServer bên dưới và khai báo lại
 *   spring.security.oauth2.resourceserver.jwt.issuer-uri trong application.yml.
 */
@Configuration("subscriptionSecurityConfig")
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
        // .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {})); // bật lại khi dùng Keycloak

        return http.build();
    }
}
