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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/auth/me")
    public ResponseEntity<ApiResponse<MeAuthResponseDto>> me(@AuthenticationPrincipal PrincipalDetails principal) {


        User user = principal.getUser();
        MeAuthResponseDto result = userService.getMeAuth(user);

        return ResponseEntity.ok(ApiResponse.ok("로그인 사용자 정보", result));
    }

   
    // 로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest req, HttpServletResponse res) {


        userService.logout(req, res);

        return ResponseEntity.ok(ApiResponse.ok("로그아웃 완료", null));
    }


    @PostMapping("/auth/withdraw")
    public ResponseEntity<ApiResponse<?>> withdraw(@AuthenticationPrincipal PrincipalDetails principal, @RequestBody WithdrawRequestDto req, HttpServletRequest request,
                                                   HttpServletResponse response) {


        User user = principal.getUser();
        String password = (req != null ? req.getPassword(): null);

        userService.withdraw(user, password, request, response);

        return ResponseEntity.ok(ApiResponse.ok("회원탈퇴 완료", null));
    }
}
