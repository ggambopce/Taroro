package com.neocompany.taroro.global.sessions;

import java.security.Principal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * WebSocket STOMP 인증 Principal
 * - WebSocketHandshakeInterceptor 에서 생성 → STOMP Controller 에서 Principal 로 주입
 * - HTTP는 PrincipalDetails, WebSocket은 SessionPrincipal 사용
 */
@Getter
@RequiredArgsConstructor
public class SessionPrincipal implements Principal {

    private final Long userId;
    private final String email;
    private final String roles;

    /** STOMP Principal.getName() == userId (String) */
    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
