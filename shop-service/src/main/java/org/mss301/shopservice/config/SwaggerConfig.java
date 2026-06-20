package org.mss301.shopservice.config;

import org.mss301.commonservice.config.CommonSwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI shopServiceOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Shop Service API")
                        .version("1.0.0")
                        .description("""
                                API quản lý cửa hàng (Shop/Tenant) và gói dịch vụ (Subscription).

                                **Chức năng chính:**
                                - Quản lý thông tin shop (CRUD)
                                - Resolve domain → shopId (internal API)
                                - Quản lý trạng thái shop (ACTIVE / INACTIVE)
                                - Quản lý gói dịch vụ (Subscription Plan CRUD)
                                - Mua gói dịch vụ và xử lý thanh toán

                                **Lưu ý:** Nhấn nút Authorize và nhập JWT token để test các API bảo mật.
                                """));

        return CommonSwaggerConfig.applyJwtSecurity(openAPI);
    }
}
