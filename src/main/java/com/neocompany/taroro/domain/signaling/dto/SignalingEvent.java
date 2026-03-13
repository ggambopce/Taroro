package com.neocompany.taroro.domain.signaling.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * WebRTC 시그널링 이벤트 — /user/{targetUserId}/queue/signaling 으로 전송
 *
 * eventType: OFFER, ANSWER, ICE_CANDIDATE, HANG_UP
 */
@Getter
@AllArgsConstructor
public class SignalingEvent {

    private final String eventType;
    private final Long roomId;
    private final Long senderId;
    private final Object payload;   // SDP / ICE candidate JSON
    private final Instant timestamp;

    public static SignalingEvent of(String eventType, Long roomId, Long senderId, Object payload) {
        return new SignalingEvent(eventType, roomId, senderId, payload, Instant.now());
    }
}

