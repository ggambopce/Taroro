package com.neocompany.taroro.domain.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.email.EmailRequestDto;
import com.neocompany.taroro.domain.users.docs.UserControllerDocs;
import com.neocompany.taroro.domain.users.dto.LoginRequestDto;
import com.neocompany.taroro.domain.users.dto.MeAuthResponseDto;
import com.neocompany.taroro.domain.users.dto.ResetPasswordRequestDto;
import com.neocompany.taroro.domain.users.dto.SignupRequestDto;
import com.neocompany.taroro.domain.users.dto.WithdrawRequestDto;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
    private final UserService userService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody @Valid LoginRequestDto req, HttpServletResponse res) {

        userService.login(req, res);
        return ResponseEntity.ok(ApiResponse.ok("로그인 성공", null));
    }

    /**
     * 일반로그인 회원가입 컨트롤러
     * @req 이메일, 이메일 인증, 패스워드, 패스워드 확인
     */
    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody @Valid SignupRequestDto req){
        userService.signup(req);
        return ResponseEntity.ok(ApiResponse.ok("회원가입 성공", null));
    }

    /**
     * 이메일 중복확인 컨트롤러
     * @req 이메일
     * @return boolean
     */
    @Override
    @PostMapping("/duplications/email")
    public ResponseEntity<ApiResponse<Boolean>> dupEmail(@RequestBody @Valid EmailRequestDto req){
        boolean result = userService.isEmailDuplicated(req.getEmail());
        String message = result ? "이미 사용중인 이메일입니다." : "사용가능 이메일입니다.";

        return ResponseEntity.ok(ApiResponse.ok(message, result));
    }

    /**
     * 이메일 인증코드 요청 컨트롤러
     * req 이메일
     * @return 이메일로 코드 발송
     */
    @Override
    @PostMapping("/email/verification")
    public ResponseEntity<ApiResponse<?>> send(@RequestBody @Valid EmailRequestDto req) {
        userService.sendVerificationCode(req.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("이메일 코드 발송 성공", null));
    }

    /**
     * 이메일 인증코드 확인 컨트롤러
     * @req 이메일
     * @return boolean
     */
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Boolean>> verify(@RequestBody @Valid EmailVerifyRequestDto req){
        boolean result = userService.verifyCode(req.getEmail(), req.getCode());
        String message = result ? "인증을 성공했습니다." : "코드가 일치하지 않습니다.";
        return ResponseEntity.ok(ApiResponse.ok(message, result));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeAuthResponseDto>> me(@AuthenticationPrincipal PrincipalDetails principal) {
        User user = principal.getUser();
        MeAuthResponseDto result = userService.getMeAuth(user);
        return ResponseEntity.ok(ApiResponse.ok("로그인 사용자 정보", result));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest req, HttpServletResponse res) {
        userService.logout(req, res);
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 완료", null));
    }

    // 비밀번호 변경
    @Override
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody @Valid ResetPasswordRequestDto req
    ) {

        // 인증된 사용자 이메일 기반으로 비밀번호 변경
        userService.resetPassword(req);
        return ResponseEntity.ok(ApiResponse.ok("비밀번호 변경 성공", null));
    }

    @Override
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

    
}
