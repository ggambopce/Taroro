package com.neocompany.taroro.domain.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.users.dto.MeAuthResponseDto;
import com.neocompany.taroro.domain.users.dto.WithdrawRequestDto;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.ApiResponse;
import com.neocompany.taroro.global.sessions.SessionCookieUtil;
import com.neocompany.taroro.global.sessions.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SessionService sessionService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeAuthResponseDto>> me(@AuthenticationPrincipal PrincipalDetails principal) {


        User user = principal.getUser();
        MeAuthResponseDto result = userService.getMeAuth(user);

        return ResponseEntity.ok(ApiResponse.ok("로그인 사용자 정보", result));
    }

   
    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest req, HttpServletResponse res) {

        String sid = SessionCookieUtil.readCookie(req, "SID");
        // 1) DB 세션 삭제
        sessionService.deleteSession(sid);
        // 2) 쿠키 삭제(도메인/경로/secure/samesite 조합이 동일해야 잘 지워짐)
        SessionCookieUtil.clearSidCookie(res, isHttps(req));

        return ResponseEntity.ok(ApiResponse.ok("로그아웃 완료", null));
    }


    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody(required = false) WithdrawRequestDto body,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        User user = principal.getUser();
        String password = (body != null ? body.getPassword() : null);

        userService.withdraw(user, password, request, response);

        return ResponseEntity.ok(ApiResponse.ok("회원탈퇴 완료", null));
    }

    private boolean isHttps(HttpServletRequest req) {
        String proto = req.getHeader("X-Forwarded-Proto");
        return "https".equalsIgnoreCase(proto) || req.isSecure();
    }
}
