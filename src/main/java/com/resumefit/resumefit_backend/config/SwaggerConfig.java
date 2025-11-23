package com.resumefit.resumefit_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme =
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                        .description("JWT 토큰을 입력해주세요. (Bearer 접두사 자동 추가)");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .info(
                        new Info()
                                .title("ResumeFit API")
                                .version("1.0.0")
                                .description("AI 기반 이력서-채용공고 매칭 서비스 API 문서")
                                .contact(
                                        new Contact()
                                                .name("ResumeFit Team")
                                                .email("support@resumefit.com")))
                .servers(List.of(new Server().url("/").description("현재 서버")))
                .components(new Components().addSecuritySchemes("BearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
