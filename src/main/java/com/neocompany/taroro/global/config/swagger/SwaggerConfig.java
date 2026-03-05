package com.neocompany.taroro.global.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
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
    public OpenAPI openAPI() {
        // SID 쿠키 인증 스킴 정의
        SecurityScheme sidCookie = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("SID")
                .description("세션 쿠키 (로그인 후 자동 발급)");

        return new OpenAPI()
                .info(new Info()
                        .title("타로 온라인 API")
                        .version("1.0.0")
                        .description("타로 온라인 백엔드 API 문서"))
                .servers(List.of(
                        new Server().url("https://taro.neocompany.co.kr/api").description("운영 서버"),
                        new Server().url("https://supretest.taro.neocompany.co.kr/api").description("테스트 서버"),
                        new Server().url("http://localhost:8080/api").description("로컬 개발")
                ))
                .components(new Components()
                        .addSecuritySchemes("SID", sidCookie))
                .addSecurityItem(new SecurityRequirement().addList("SID"));
    }
}
