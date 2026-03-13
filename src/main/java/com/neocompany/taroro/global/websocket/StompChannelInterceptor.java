package com.neocompany.taroro.global.websocket;

import java.security.Principal;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * STOMP 프레임 레벨 권한 검사
 *
 * CONNECT  - Principal 존재 확인
 * SUBSCRIBE - 허용된 destination 패턴 검사
 * SEND     - /app/ 접두사 검사
 */
@Slf4j
@Component
public class StompChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) return message;

        switch (accessor.getCommand()) {
            case CONNECT   -> checkConnect(accessor);
            case SUBSCRIBE -> checkSubscribe(accessor);
            case SEND      -> checkSend(accessor);
            default        -> { /* ACK, NACK 등은 통과 */ }
        }
        return message;
    }

    // ── CONNECT ──────────────────────────────────────────────────────────────

    private void checkConnect(StompHeaderAccessor accessor) {
        if (accessor.getUser() == null) {
            log.warn("[STOMP] CONNECT blocked - no principal");
            throw new MessageDeliveryException("인증되지 않은 연결입니다.");
        }
        log.info("[STOMP] CONNECT - user={}", accessor.getUser().getName());
    }

    // ── SUBSCRIBE ─────────────────────────────────────────────────────────────

    private void checkSubscribe(StompHeaderAccessor accessor) {
        Principal user = requireAuth(accessor);
        String dest = accessor.getDestination();

        if (!isAllowedSubscription(dest)) {
            log.warn("[STOMP] SUBSCRIBE blocked - user={}, dest={}", user.getName(), dest);
            throw new MessageDeliveryException("구독 권한이 없습니다: " + dest);
        }
        log.info("[STOMP] SUBSCRIBE - user={}, dest={}", user.getName(), dest);
    }

    /**
     * 허용 패턴
     *   /user/queue/**          - 자신의 개인 큐 (에러/알림/시그널링 포함)
     *   /topic/test             - 테스트 브로드캐스트
     *   /topic/rooms/{roomId}   - 방 이벤트 (메시지/읽음/타이핑/상태 변경)
     *   /topic/waiting-room     - 대기열 이벤트
     *   /topic/masters/status   - 마스터 상태 변경
     */
    private boolean isAllowedSubscription(String dest) {
        if (dest == null) return false;
        return dest.startsWith("/user/queue/")
                || dest.equals("/topic/test")
                || dest.startsWith("/topic/rooms/")
                || dest.equals("/topic/waiting-room")
                || dest.equals("/topic/masters/status");
    }

    // ── SEND ──────────────────────────────────────────────────────────────────

    private void checkSend(StompHeaderAccessor accessor) {
        Principal user = requireAuth(accessor);
        String dest = accessor.getDestination();

        if (dest == null || !dest.startsWith("/app/")) {
            log.warn("[STOMP] SEND blocked - user={}, dest={}", user.getName(), dest);
            throw new MessageDeliveryException("허용되지 않은 destination: " + dest);
        }
    }

    // ── 공통 ──────────────────────────────────────────────────────────────────

    private Principal requireAuth(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        if (user == null) {
            throw new MessageDeliveryException("인증이 필요합니다.");
        }
        return user;
    }
}
