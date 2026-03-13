package com.neocompany.taroro.domain.message.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 읽음 처리 이벤트 — /topic/room.{roomId} 브로드캐스트
 */
@Getter
@AllArgsConstructor
public class ReadUpdatedEvent {

    private final String eventType = "READ_UPDATED";
    private final Long roomId;
    private final Long userId;
    private final Long lastReadMessageId;
}
