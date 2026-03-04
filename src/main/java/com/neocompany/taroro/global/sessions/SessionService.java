package com.neocompany.taroro.global.sessions;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    private static final SecureRandom RNG = new SecureRandom();


    /**
     * 세션 생성: 예측 불가능한 sessionId 발급 후 DB 저장
     */
    @Transactional
    public String createSession(Long userId, Duration ttl) {
        String sid = generateSid();

        Session s = new Session();
        s.setSessionId(sid);
        s.setUserId(userId);
        s.setExpiresAt(Instant.now().plusSeconds(ttl.getSeconds()));
        s.setLastAccessAt(Instant.now());

        sessionRepository.save(s);
        return sid;
    }

    @Transactional
    public void deleteSession(String sid) {
        if (sid == null || sid.isBlank()) return;
        sessionRepository.deleteBySessionId(sid);
    }

    /**
     * 만료면 삭제하고 empty 처리하는 방식은 Filter에서 수행
     */
    public static String generateSid() {
        // 48바이트 랜덤 → URL-safe base64(길이 약 64)
        byte[] bytes = new byte[48];
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}