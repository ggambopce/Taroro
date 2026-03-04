package com.neocompany.taroro.global.sessions;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.neocompany.taroro.domain.users.UserRepository;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String sid = SessionCookieUtil.readCookie(req, "SID");

        if (sid != null && !sid.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            sessionRepository.findBySessionId(sid).ifPresentOrElse(session -> {

                // 만료면 세션 삭제 + 익명 처리
                if (session.getExpiresAt().isBefore(Instant.now())) {
                    sessionRepository.delete(session);
                    clearContextIfNeeded();
                    log.debug("Expired session deleted sid={}", sid);
                    return;
                }

                userRepository.findById(session.getUserId()).ifPresentOrElse(user -> {

                    if (user.isDeleted()) {
                        // 탈퇴 계정이면 세션 제거
                        sessionRepository.delete(session);
                        clearContextIfNeeded();
                        log.debug("Deleted user session removed userId={}", user.getUserId());
                        return;
                    }

                    var principal = new PrincipalDetails(user);
                    var auth = new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // lastAccess 업데이트(필요시)
                    session.setLastAccessAt(Instant.now());
                    sessionRepository.save(session);

                }, () -> {
                    // 사용자 없으면 세션 제거
                    sessionRepository.delete(session);
                    clearContextIfNeeded();
                });

            }, () -> {
                // SID는 있는데 DB에 없음
                clearContextIfNeeded();
            });
        }

        chain.doFilter(req, res);
    }

    private void clearContextIfNeeded() {
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        return p.startsWith("/oauth2/")
                || p.startsWith("/login/oauth2/")
                || p.startsWith("/api/auth/login")
                || p.startsWith("/api/auth/signup")
                || p.startsWith("/api/auth/logout")
                || p.startsWith("/api/auth/email/")
                || p.startsWith("/error")
                || p.startsWith("/assets/")
                || p.startsWith("/css/")
                || p.startsWith("/js/")
                || p.startsWith("/images/")
                || p.startsWith("/favicon/");
    }
}
