package com.ssafy.lumiroom.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String JWT_SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addServersItem(new Server().url("http://localhost:8080"))
                .addTagsItem(new Tag().name("Infra").description("치안 인프라 API"))
                .addTagsItem(new Tag().name("Property").description("부동산 API"))
                .addTagsItem(new Tag().name("Safety").description("안전 정보 API"))
                .addTagsItem(new Tag().name("Auth").description("인증 API"))
                .addTagsItem(new Tag().name("User").description("사용자 API"))
                .components(new Components()
                        .addSecuritySchemes(JWT_SECURITY_SCHEME_NAME, jwtSecurityScheme()))
                .addSecurityItem(new SecurityRequirement().addList(JWT_SECURITY_SCHEME_NAME));
    }

    private Info apiInfo() {
        return new Info()
                .title("LumiRoom API")
                .description("안전 특화 부동산 서비스 API 문서")
                .version("v1.0");
    }

    private SecurityScheme jwtSecurityScheme() {
        return new SecurityScheme()
                .name(JWT_SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
