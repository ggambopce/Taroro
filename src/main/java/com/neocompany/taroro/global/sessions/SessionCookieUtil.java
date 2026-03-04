package com.neocompany.taroro.global.sessions;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SessionCookieUtil {
    public static void writeSidCookies(HttpServletResponse res, String sid, boolean secure) {
        // https 운영: None, http 로컬: Lax(또는 None 시도)
        String sameSite = secure ? "None" : "Lax";
        ResponseCookie cookie = ResponseCookie.from("SID", sid)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ofDays(365)) // 요구사항: 1년
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void clearSidCookie(HttpServletResponse res, boolean secure) {
        String sameSite = secure ? "None" : "Lax";
        ResponseCookie cookie = ResponseCookie.from("SID", "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static String readCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        for (var c : req.getCookies()) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
