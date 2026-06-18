package org.mss301.paymentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentSwaggerConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .version("1.0.0")
                        .description("""
                                Dịch vụ thanh toán (mock) dùng chung cho toàn hệ thống.

                                **Chức năng chính:**
                                - Tạo đơn thanh toán (trả về link thanh toán giả lập)
                                - Xác nhận thanh toán (/confirm) — giả lập người dùng trả tiền thành công
                                - Khi thành công, gọi callback về service nguồn (vd subscription-service)

                                Service này KHÔNG biết về nghiệp vụ subscription/order — chỉ thu tiền theo orderCode.
                                """));
    }
}
