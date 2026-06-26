package org.mss301.inventoryservice.config;

import org.mss301.commonservice.config.CommonSwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI inventoryServiceOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Inventory Service API")
                        .version("1.0.0")
                        .description("""
                                API quản lý kho hàng và nguyên liệu.

                                **Chức năng chính:**
                                - Quản lý nguyên liệu thô (Raw Ingredients)
                                - Quản lý hóa đơn nhập kho (Inventory Invoices)
                                - Quản lý kiểm kho (Stock Checks)
                                - Chuyển đổi đơn vị (Unit Conversions)

                                **Lưu ý:** Nhấn nút Authorize và nhập JWT token để test các API bảo mật.
                                """));

        return CommonSwaggerConfig.applyJwtSecurity(openAPI);
    }
}
