package com.neocompany.taroro.global.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.neocompany.taroro.global.sessions.SessionCookieUtil;
import com.neocompany.taroro.global.sessions.SessionService;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final SessionService sessionService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication)
            throws IOException {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long userId = principal.getUser().getUserId();

        // 기존 SID 폐기
        String oldSid = SessionCookieUtil.readCookie(req, "SID");
        sessionService.deleteSession(oldSid);

        // 세션 생성 + 쿠키 발급
        String sid = sessionService.createSession(userId, Duration.ofDays(365));
        SessionCookieUtil.writeSidCookies(res, sid, isHttps(req));

        String redirectUri = getFrontendSuccessRedirect(req);
        res.sendRedirect(redirectUri);
    }

    private boolean isHttps(HttpServletRequest req) {
        String proto = req.getHeader("X-Forwarded-Proto");
        return "https".equalsIgnoreCase(proto) || req.isSecure();
    }

    private String getFrontendSuccessRedirect(HttpServletRequest req) {
        if (isHttps(req)) return "https://matatabi-pkbe.vercel.app/auth/callback";
        return "http://localhost:5174/auth/callback";
    }

}
