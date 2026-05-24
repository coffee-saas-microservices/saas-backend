package org.mss301.identityservice.config;

import org.mss301.commonservice.config.CommonSwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentitySwaggerConfig {

    @Bean
    public OpenAPI identityServiceOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Identity Service API")
                        .version("1.0.0")
                        .description("""
                                API quản lý xác thực và phân quyền người dùng.

                                **Chức năng chính:**
                                - Đăng ký / Đăng nhập người dùng
                                - Quản lý JWT token (issue, refresh, revoke)
                                - Phân quyền dựa trên Role (RBAC)
                                - Tích hợp Keycloak / OAuth2

                                **Lưu ý:** Nhấn nút Authorize và nhập JWT token để test các API bảo mật.
                                """));

        return CommonSwaggerConfig.applyJwtSecurity(openAPI);
    }
}
