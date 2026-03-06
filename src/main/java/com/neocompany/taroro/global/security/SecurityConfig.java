package com.neocompany.taroro.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.neocompany.taroro.global.oauth2.CustomOauth2UserService;
import com.neocompany.taroro.global.oauth2.Oauth2FailureHandler;
import com.neocompany.taroro.global.oauth2.Oauth2SuccessHandler;
import com.neocompany.taroro.global.sessions.SessionAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SessionAuthenticationFilter sessionAuthenticationFilter;
    private final CustomOauth2UserService customOauth2UserService;
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final Oauth2FailureHandler oauth2FailureHandler;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .formLogin(form -> form.disable())     // 기본 로그인 페이지 비활성화
                // 서버 기본 HttpSession은 쓰지 않음(커스텀 세션 DB로만 인증 복원)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))) // 인증 실패(미인증 접근) 시 401 응답 반환
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // Swagger UI
                                "/swagger", "/swagger/**", "/swagger-ui/**",
                                "/api-docs", "/api-docs/**").permitAll()
                        .requestMatchers(
                                // OAuth2 진입/콜백
                                "/oauth2/authorization/**", "/login/oauth2/code/**").permitAll()
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/email/verification",
                                "/api/auth/email/verify",
                                "/api/auth/logout",
                                "/api/auth/password/reset"
                        ).permitAll() // 허용
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/toss-check.html", "/success.html", "/point-check.html",
                                "/favicon/**",
                                // Vite 기본 산출물들
                                "/assets/**",      // JS/CSS 번들
                                "/vite.svg",       // Vite 아이콘
                                "/*.css",          // /index.css 같은 루트 CSS
                                "/*.js",           // 루트 JS가 있다면
                                "/fonts/**",
                                "/img/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/admin/**",
                                "/api/support/posts/answer/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                "/api/auth/me",
                                "/api/auth/withdraw",
                                "/api/auth/logout",
                                "/api/point/charge/toss/ready",
                                "/api/point/charge/toss/confirm"
                        ).authenticated() // 사용자 인증 필수
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOauth2UserService))
                        .successHandler(oauth2SuccessHandler)
                        .failureHandler(oauth2FailureHandler)
                )
                .addFilterBefore(sessionAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.disable());
        return http.build();
    }


}
