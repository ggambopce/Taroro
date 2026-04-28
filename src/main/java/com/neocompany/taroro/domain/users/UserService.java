package com.neocompany.taroro.domain.users;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.mail.MailException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.email.EmailService;
import com.neocompany.taroro.domain.email.InMemoryEmailVerificationStore;
import com.neocompany.taroro.domain.users.dto.LoginRequestDto;
import com.neocompany.taroro.domain.users.dto.MeAuthResponseDto;
import com.neocompany.taroro.domain.users.dto.ResetPasswordRequestDto;
import com.neocompany.taroro.domain.users.dto.SignupRequestDto;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;
import com.neocompany.taroro.global.sessions.SessionCookieUtil;
import com.neocompany.taroro.global.sessions.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InMemoryEmailVerificationStore emailStore;
    private final EmailService emailService;
    private final SessionService sessionService;

    private static final boolean SECURE_COOKIE = false; // 운영 HTTPS면 true
    private static final Duration SESSION_TTL = Duration.ofDays(365);

    @Transactional
    public void login(LoginRequestDto req, HttpServletResponse res) {
        // 비밀번호와 디비 비밀번호 일치 확인
        User user = verifyCredentials(req);

        if (user.isDeleted()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "탈퇴한 계정입니다.");
        }

        // SID 발급
        String sid = sessionService.createSession(user.getUserId(), SESSION_TTL);
        // 쿠키 세팅
        SessionCookieUtil.writeSidCookies(res, sid, SECURE_COOKIE);
    }

    @Transactional
    public void adminLogin(LoginRequestDto req, HttpServletResponse res) {
        User user = verifyCredentials(req);

        if (user.isDeleted()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "탈퇴한 계정입니다.");
        }
        if (!user.getRoles().contains("ROLE_ADMIN")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "관리자 권한이 없습니다.");
        }

        String sid = sessionService.createSession(user.getUserId(), SESSION_TTL);
        SessionCookieUtil.writeSidCookies(res, sid, SECURE_COOKIE);
    }

    /**
     * 비밀번호와 디비 비밀번호 일치 확인
     */
    @Transactional(readOnly = true)
    public User verifyCredentials(@Valid LoginRequestDto req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "이메일을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    /**
     * 이메일 중복 여부 확인.
     * @req email 사용자 이메일
     * @return true면 이미 존재함.
     */
    @Transactional
    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 인증코드를 생성하고 이메일로 전송한다.
     *  1) 6자리 난수 코드 생성 (000000~999999)
     *  2) InMemoryEmailVerificationStore 에 코드 저장 (유효기간 10분)
     *  3) MailService 를 통해 수신자 이메일로 발송
     */
    public void sendVerificationCode(String email) {

        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
        emailStore.save(email, code, Duration.ofMinutes(10));

        // Google Mail 호출 실패시 예외처리
        try {
            emailService.sendVerificationCode(email, code);
        } catch (MailException e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "인증 메일 전송 중 오류가 발생했습니다.");
        }

    }

    /**
     * 사용자가 입력한 인증코드를 검증하고 일치 시 verified=true로 표시한다.
     * @param email 이메일 주소
     * @param code 사용자가 입력한 코드
     * @return 일치하면 true, 불일치/만료되면 false
     */
    public boolean verifyCode(String email, String code) {
        return emailStore.checkAndMarkVerified(email, code); // 일치 시 ture 저장
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto req) {
        // 비밀번호 확인
        if(!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 이메일 인증 여부 확인
        if(!emailStore.isVerified(req.getEmail())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "이메일 인증이 필요합니다.");
        }
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REQUEST, "이메일에 해당하는 사용자를 찾을 수 없습니다."));

        // userName 검증
        if (!user.getName().equals(req.getName())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이메일과 사용자 이름이 일치하지 않습니다.");
        }

        // 비밀번호 암호화 후 저장
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
    }

    
     /**
     * 일반 회원가입
     */
    @Transactional
    public void signup(SignupRequestDto requestDto) {

        // 이메일 중복 검사
        if(userRepository.existsByEmail(requestDto.getEmail())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 가입된 이메일입니다.");
        }
        if(!emailStore.isVerified(requestDto.getEmail())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이메일 인증이 필요합니다.");
        }

        if(!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        User u = new User();
        u.setEmail(requestDto.getEmail());
        u.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        u.setName(requestDto.getName());
        u.setRoles("ROLE_USER");
        u.setLoginType("normal");

        userRepository.save(u);
        
    }


    /**
     * 로그아웃 처리
     * 1) RT 쿠키 읽어서 서버측 로그아웃(블랙리스트/삭제) 처리
     * 2) AT/RT/FAM 쿠키 제거
     * 3) 세션 무효화 + SecurityContext 정리
     */
    public void logout(HttpServletRequest req, HttpServletResponse res) {

        // 1) SID 쿠키로 DB 세션 삭제
        String sid = SessionCookieUtil.readCookie(req, "SID");
        if (sid != null && !sid.isBlank()) {
            sessionService.deleteSession(sid);
        }
        // 2) SID 쿠키 제거
        SessionCookieUtil.clearSidCookie(res, SECURE_COOKIE);
        // 3) SecurityContext 정리
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
            if (password == null || password.isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "현재 비밀번호가 필요합니다.");
            }
            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
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
