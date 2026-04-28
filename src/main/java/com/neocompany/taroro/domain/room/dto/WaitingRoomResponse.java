package com.neocompany.taroro.domain.room.dto;

import java.time.Instant;
import java.util.List;

import lombok.Getter;

@Getter
public class WaitingRoomResponse {

    private final long waitingCount;
    private final List<WaitingRoomItem> items;

    public WaitingRoomResponse(long waitingCount, List<WaitingRoomItem> items) {
        this.waitingCount = waitingCount;
        this.items = items;
    }

    public record WaitingRoomItem(
            int queueNumber,
            Long roomId,
            Long masterId,
            String masterName,
            String roomName,
            Instant requestedAt
    ) {}
}
