package com.neocompany.taroro.global.websocket;

import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.neocompany.taroro.global.redis.RedisKeys;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final StringRedisTemplate redis;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String userIdStr = redis.opsForValue().get(RedisKeys.stompSession(sessionId));
        if (userIdStr != null) {
            Long userId = Long.parseLong(userIdStr);
            redis.delete(RedisKeys.userOnline(userId));
            redis.delete(RedisKeys.stompSession(sessionId));
            log.info("[STOMP] DISCONNECT userId={}", userId);
        }
    }
}
