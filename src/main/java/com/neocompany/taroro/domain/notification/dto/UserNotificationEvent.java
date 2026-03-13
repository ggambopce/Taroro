package com.neocompany.taroro.domain.notification.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 개인 알림 이벤트 — /user/{userId}/queue/notifications 으로 전송
 *
 * eventType 예시: ROOM_CREATED, CALLED, PAYMENT_COMPLETED, POINT_CHARGED
 */
@Getter
@AllArgsConstructor
public class UserNotificationEvent {

    private final String eventType;
    private final String title;
    private final String body;
    private final Object data;      // 추가 페이로드 (roomId, amount 등)
    private final Instant timestamp;

    public static UserNotificationEvent of(String eventType, String title, String body) {
        return new UserNotificationEvent(eventType, title, body, null, Instant.now());
    }

    public static UserNotificationEvent of(String eventType, String title, String body, Object data) {
        return new UserNotificationEvent(eventType, title, body, data, Instant.now());
    }
}
