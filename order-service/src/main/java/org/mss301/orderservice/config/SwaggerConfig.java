package org.mss301.orderservice.config;

import org.mss301.commonservice.config.CommonSwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .version("1.0.0")
                        .description("""
                                API quản lý đơn hàng và thanh toán.

                                **Chức năng chính:**
                                - Tạo đơn hàng (Create Order)
                                - Xử lý Saga thanh toán & trừ kho
                                - Lấy lịch sử đơn hàng
                                """));

        return CommonSwaggerConfig.applyJwtSecurity(openAPI);
    }
}
