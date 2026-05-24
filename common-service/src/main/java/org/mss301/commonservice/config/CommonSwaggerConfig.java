package org.mss301.commonservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

public class CommonSwaggerConfig {

        public static final String SECURITY_SCHEME_NAME = "bearerAuth";

        public static OpenAPI applyJwtSecurity(OpenAPI openAPI) {
                SecurityScheme bearerScheme = new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Nhập JWT token (không cần prefix 'Bearer ')");

                SecurityRequirement globalSecurityRequirement = new SecurityRequirement().addList(SECURITY_SCHEME_NAME);

                return openAPI
                                .components(new Components()
                                                .addSecuritySchemes(SECURITY_SCHEME_NAME, bearerScheme))
                                .addSecurityItem(globalSecurityRequirement);
        }
}
