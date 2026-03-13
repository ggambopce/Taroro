package com.neocompany.taroro.domain.notification;

import org.springframework.stereotype.Service;

import com.neocompany.taroro.domain.notification.dto.UserNotificationEvent;
import com.neocompany.taroro.global.websocket.StompDestination;
import com.neocompany.taroro.global.websocket.StompEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEventPublishService {

    private final StompEventPublisher publisher;

    /**
     * 특정 유저에게 개인 알림 전송
     * → /user/{userId}/queue/notifications
     */
    public void publish(Long userId, UserNotificationEvent event) {
        publisher.sendToUser(String.valueOf(userId), StompDestination.USER_NOTIFICATIONS, event);
    }
}
