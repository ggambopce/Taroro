package com.neocompany.taroro.domain.room.dto;

import java.time.Instant;

import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.entity.RoomStatus;

import lombok.Getter;

@Getter
public class RoomResponse {

    private final Long id;
    private final Long userId;
    private final Long masterId;
    private final RoomStatus status;
    private final Instant startedAt;
    private final Instant endedAt;
    private final Instant createdAt;

    public RoomResponse(Room room) {
        this.id = room.getId();
        this.userId = room.getUserId();
        this.masterId = room.getMasterId();
        this.status = room.getStatus();
        this.startedAt = room.getStartedAt();
        this.endedAt = room.getEndedAt();
        this.createdAt = room.getCreatedAt();
    }
}
