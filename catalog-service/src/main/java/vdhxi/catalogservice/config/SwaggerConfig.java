package vdhxi.catalogservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.mss301.commonservice.config.CommonSwaggerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI catalogServiceOpenApi() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("")
                        .version(""));
        return CommonSwaggerConfig.applyJwtSecurity(openAPI);
    }
}
