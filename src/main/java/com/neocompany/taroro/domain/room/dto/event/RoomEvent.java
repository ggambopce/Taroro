package com.neocompany.taroro.domain.room.dto.event;

import java.time.Instant;

import com.neocompany.taroro.domain.room.entity.RoomStatus;

import lombok.Builder;
import lombok.Getter;

// RoomEventType 은 같은 패키지의 RoomEventType.java 참조

/**
 * 방 상태 변경 이벤트 — /topic/room.{roomId} 로 브로드캐스트
 */
@Getter
@Builder
public class RoomEvent {

    private final String eventType;   // ROOM_ENTER, ROOM_LEAVE, ROOM_START, ROOM_END
    private final Long roomId;
    private final Long triggeredBy;   // 이벤트를 발생시킨 userId
    private final RoomStatus roomStatus;
    private final Instant timestamp;

    public static RoomEvent of(RoomEventType type, Long roomId, Long triggeredBy, RoomStatus roomStatus) {
        return RoomEvent.builder()
                .eventType(type.name())
                .roomId(roomId)
                .triggeredBy(triggeredBy)
                .roomStatus(roomStatus)
                .timestamp(Instant.now())
                .build();
    }
}
