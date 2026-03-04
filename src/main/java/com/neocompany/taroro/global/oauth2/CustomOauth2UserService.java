package com.neocompany.taroro.global.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.neocompany.taroro.domain.users.User;
import com.neocompany.taroro.domain.users.UserRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOauth2UserService 유저 정보 저장 시작");

        try {
            // 소셜 사용자 정보 조회
            OAuth2User user = super.loadUser(userRequest);
            Map<String, Object> attributes = user.getAttributes();
            log.info("소셜 raw attributes 데이터 확인 = {}", attributes);

            // 소셜로그인 회사 확인
            String requestId = userRequest.getClientRegistration().getRegistrationId();  // 요청한 oath2 사이트 회사명

            log.info("로그인 시도 소셜로그인 회사={}",requestId);
            Oauth2UserInfo oui = null;
            User u= null;

            if (requestId.equals("google")) {
                oui = new GoogleUserInfo(attributes);
                u = saveOrLogin(oui.getId(), oui.getEmail(), oui.getName(), requestId);
            } else if (requestId.equals("kakao")) {
                oui = new KakaoUserInfo(attributes);
                u = saveOrLogin(oui.getId(), oui.getEmail(), oui.getName(), requestId);
            } else if (requestId.equals("naver")) {
                oui = new NaverUserInfo(attributes);
                u = saveOrLogin(oui.getId(), oui.getEmail(), oui.getName(), requestId);

            }
            return new PrincipalDetails(u);
        } catch (BusinessException e) {
            // BusinessException → OAuth2AuthenticationException 변환
            OAuth2Error error = new OAuth2Error(
                    e.getErrorCode().name(),      // error code
                    e.getMessage(),               // description
                    null
            );
            throw new OAuth2AuthenticationException(error, e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예상치 못한 예외 처리
            OAuth2Error error = new OAuth2Error(
                    "oauth2_internal_error",
                    "소셜 로그인 처리 중 알 수 없는 오류가 발생했습니다.",
                    null
            );
            throw new OAuth2AuthenticationException(error, error.getDescription(), e);
        }
    }

    /**
     * 규칙
     * 1) email이 DB에 있으면
     *    - loginType == "normal"  -> 소셜 로그인 금지(에러)
     *    - loginType == "google" -> 기존 계정으로 로그인 처리(그대로 리턴)
     * 2) email이 없으면 신규 가입
     */
    private User saveOrLogin(String id, String email, String name,  String requestId) {

        return userRepository.findByEmail(email)
                // deleted = true 인 탈퇴 계정은 조회 제외
                .filter(existing -> !existing.isDeleted())
                .map(existing -> {
                    // 기존 회원 존재
                    if ("normal".equalsIgnoreCase(existing.getLoginType())) {
                        // 일반 로그인 회원이면 소셜 로그인 금지
                        throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 가입된 이메일입니다. 일반 로그인 방식을 사용하세요");
                    }
                    if ("google".equalsIgnoreCase(existing.getLoginType())) {
                        // 이미 구글 로그인 회원이면 그대로 로그인
                        return existing;
                    }
                    if ("kakao".equalsIgnoreCase(existing.getLoginType())) {
                        // 이미 구글 로그인 회원이면 그대로 로그인
                        return existing;
                    }
                    if ("naver".equalsIgnoreCase(existing.getLoginType())) {
                        // 이미 구글 로그인 회원이면 그대로 로그인
                        return existing;
                    }
                    // 혹시 다른 값이 있을 때 (방어적 처리)
                    throw new BusinessException(ErrorCode.INVALID_REQUEST, "해당 이메일은 소셜 로그인으로 가입되어 있지 않습니다."
                    );
                })
                .orElseGet(() -> {
                    // 신규 가입
                    User u = new User();
                    u.setEmail(email);
                    u.setLoginType(requestId);
                    u.setName(name);
                    u.setRoles("ROLE_USER");
                    u.setPasswordHash(passwordEncoder.encode("google:" + id));
                    u.setDeleted(false);
                    u.setDeletedAt(null);
                    User savedUser = userRepository.save(u);

                    return savedUser;
                });
    }
}
