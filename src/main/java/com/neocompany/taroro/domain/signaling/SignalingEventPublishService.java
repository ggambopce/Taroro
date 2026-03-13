package com.neocompany.taroro.domain.signaling;

import org.springframework.stereotype.Service;

import com.neocompany.taroro.domain.signaling.dto.SignalingEvent;
import com.neocompany.taroro.global.websocket.StompDestination;
import com.neocompany.taroro.global.websocket.StompEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignalingEventPublishService {

    private final StompEventPublisher publisher;

    /**
     * WebRTC 시그널링 메시지를 특정 유저에게 전송
     * → /user/{targetUserId}/queue/signaling
     *
     * @param targetUserId 수신 대상 userId
     * @param event        OFFER / ANSWER / ICE_CANDIDATE / HANG_UP
     */
    public void publish(Long targetUserId, SignalingEvent event) {
        publisher.sendToUser(String.valueOf(targetUserId), StompDestination.USER_SIGNALING, event);
    }
}
