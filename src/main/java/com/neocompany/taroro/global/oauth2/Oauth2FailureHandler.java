package com.neocompany.taroro.global.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class Oauth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException exception)
            throws IOException, ServletException {

        log.warn("소셜 로그인 실패: {}", exception.getMessage());

        String error = encode("oauth_failed");
        String message = encode(exception.getMessage() == null ? "social login failed" : exception.getMessage());

        String redirectUri = getFrontendFailureRedirect(req, error, message);

        log.info("OAuth2 로그인 실패 → redirect: {}", redirectUri);
        res.sendRedirect(redirectUri);
    }

    private String getFrontendFailureRedirect(HttpServletRequest req, String error, String message) {
        boolean https = "https".equalsIgnoreCase(req.getHeader("X-Forwarded-Proto")) || req.isSecure();

        if (https) {
            return "https://matatabi-pkbe.vercel.app/auth/callback?error=" + error + "&message=" + message;
        }
        return "http://localhost:5174/auth/callback?error=" + error + "&message=" + message;
    }

    private String encode(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}