package org.mss301.subscriptionservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.mss301.commonservice.config.CommonSwaggerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriptionSwaggerConfig {

    @Bean
    public OpenAPI subscriptionServiceOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Subscription Service API")
                        .version("1.0.0")
                        .description("""
                                API quản lý gói dịch vụ (Subscription Plan) trong hệ thống SaaS.

                                **Chức năng chính:**
                                - Tạo, cập nhật, xóa (mềm) gói dịch vụ
                                - Tra cứu danh sách gói dịch vụ đang hoạt động
                                - Lưu cấu hình giới hạn tính năng (configLimit) dạng JSON

                                **Lưu ý:** Nhấn nút Authorize và nhập JWT token để test các API bảo mật.
                                """));

        return CommonSwaggerConfig.applyJwtSecurity(openAPI);
    }
}
