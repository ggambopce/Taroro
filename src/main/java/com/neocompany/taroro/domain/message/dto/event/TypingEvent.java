package com.neocompany.taroro.domain.message.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 타이핑 이벤트 — /topic/room.{roomId} 브로드캐스트 (DB 저장 없음)
 */
@Getter
@AllArgsConstructor
public class TypingEvent {

    private final String eventType = "TYPING";
    private final Long roomId;
    private final Long userId;
    private final boolean typing;
}

