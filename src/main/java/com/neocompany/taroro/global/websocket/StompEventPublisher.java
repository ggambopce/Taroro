package com.neocompany.taroro.global.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * STOMP 이벤트 발행 유틸리티
 *
 * broadcast    → convertAndSend   → /topic/...
 * sendToUser   → convertAndSendToUser → /user/{userId}/queue/...
 */
@Component
@RequiredArgsConstructor
public class StompEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    /** 특정 destination 에 브로드캐스트 */
    public void broadcast(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }

    /**
     * 특정 사용자에게 개인 메시지 전송
     *
     * @param userId      수신 대상 userId (String)
     * @param destination /queue/... 형태 (StompDestination 상수 사용)
     * @param payload     전송할 데이터
     */
    public void sendToUser(String userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(userId, destination, payload);
    }
}
