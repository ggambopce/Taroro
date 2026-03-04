package com.neocompany.taroro.domain.users;

import java.time.Instant;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.users.dto.MeAuthResponseDto;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;
import com.neocompany.taroro.global.jwt.AuthCookieUtil;
import com.neocompany.taroro.global.jwt.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final TokenService tokenService;

    private static final boolean SECURE_COOKIE = false; // 운영 HTTPS면 true

    /**
     * 로그아웃 처리
     * 1) RT 쿠키 읽어서 서버측 로그아웃(블랙리스트/삭제) 처리
     * 2) AT/RT/FAM 쿠키 제거
     * 3) 세션 무효화 + SecurityContext 정리
     */
    public void logout(HttpServletRequest req, HttpServletResponse res) {

        // 1) 서버측 토큰 폐기 (RT가 있을 때만)
        String rt = AuthCookieUtil.readCookie(req, "RT");
        if (rt != null && !rt.isBlank()) {
            tokenService.logoutByRefreshToken(rt);
        }

        // 2) 클라이언트 쿠키 제거 (도메인/경로/보안 옵션은 기존 정책에 맞춤)
        AuthCookieUtil.deleteCookie(res, "AT", null, "/", SECURE_COOKIE, "Lax");
        AuthCookieUtil.deleteCookie(res, "RT", null, "/", SECURE_COOKIE, "Lax");
        AuthCookieUtil.deleteCookie(res, "FAM", null, "/", SECURE_COOKIE, "Lax");

        // 3) 세션/시큐리티 컨텍스트 정리 (무상태지만 방어적으로 처리)
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();

    }

    @Transactional
    public MeAuthResponseDto getMeAuth(User user) {
        User u = userRepository.findByEmail(user.getEmail())
                .orElse(null);
        return MeAuthResponseDto.of(u);
    }

   

    @Transactional
    public void withdraw(User user, String password, HttpServletRequest request, HttpServletResponse response) {
        String loginType = user.getLoginType();

        if ("normal".equals(loginType)) {
            // 비밀번호 필수
            if (password == null || password.isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "현재 비밀번호가 필요합니다.");
            }

        } else if (isSocialLogin(loginType)) {
            // 소셜로그인은 비밀번호 검증 스킵
        } else {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "지원하지 않는 로그인 타입입니다.");
        }
        // 로그아웃
        logout(request, response);
        // 유저 도메인 데이터 정리
        softDeleteUser(user);

    }

    private boolean isSocialLogin(String loginType) {
        if (loginType == null) return false;
        String t = loginType.toLowerCase();
        return t.equals("google") || t.equals("kakao") || t.equals("naver");
    }

    private void softDeleteUser(User user) {
        String anonymizedEmail = "deleted-" + user.getUserId()+ "-" + user.getEmail();

        user.setEmail(anonymizedEmail);
        user.setDeleted(true);
        user.setDeletedAt(Instant.now());

        // 필요하면 도메인 상태 정리
        userRepository.save(user);
    }

}
