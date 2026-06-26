package vdhxi.catalogservice.config;

import org.mss301.commonservice.config.CommonSwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI catalogServiceOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Catalog Service API")
                        .version("1.0.0")
                        .description("""
                                API quản lý danh mục, sản phẩm, và combo.

                                **Chức năng chính:**
                                - Quản lý danh mục (Category)
                                - Quản lý sản phẩm và biến thể (Product & Product Variant)
                                - Quản lý công thức (Recipe)
                                - Quản lý kích thước và topping (Size & Topping)
                                - Quản lý combo (Combo Item)

                                **Lưu ý:** Nhấn nút Authorize và nhập JWT token để test các API bảo mật.
                                """));

        return CommonSwaggerConfig.applyJwtSecurity(openAPI);
    }
}
