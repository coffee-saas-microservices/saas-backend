package org.mss301.employeeservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.mss301.commonservice.config.CommonSwaggerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeSwaggerConfig {

    @Bean
    public OpenAPI employeeServiceOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Employee Service API")
                        .version("1.0.0")
                        .description("""
                                API quản lý nhân viên trong hệ thống SaaS.

                                **Chức năng chính:**
                                - Quản lý thông tin nhân viên (thêm, sửa, xóa, tra cứu)
                                - Phân công vai trò nhân viên theo cửa hàng
                                - Lọc và phân trang danh sách nhân viên

                                **Lưu ý:** Nhấn nút Authorize và nhập JWT token để test các API bảo mật.
                                """));

        return CommonSwaggerConfig.applyJwtSecurity(openAPI);
    }
}
