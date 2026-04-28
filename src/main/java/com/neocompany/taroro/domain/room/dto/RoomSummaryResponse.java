package com.neocompany.taroro.domain.room.dto;

import java.time.Instant;

import com.neocompany.taroro.domain.message.entity.ChatMessage;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.entity.RoomStatus;

import lombok.Getter;

@Getter
public class RoomSummaryResponse {

    private final Long id;
    private final Long masterId;
    private final String masterName;
    private final String roomName;
    private final RoomStatus status;
    private final String lastMessage;
    private final Instant lastMessageAt;
    private final long unreadCount;
    private final Instant createdAt;

    private RoomSummaryResponse(Room room, String lastMessage, Instant lastMessageAt, long unreadCount) {
        this.id = room.getId();
        this.masterId = room.getMasterId();
        this.masterName = room.getMasterName();
        this.roomName = room.getRoomName();
        this.status = room.getStatus();
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
        this.unreadCount = unreadCount;
        this.createdAt = room.getCreatedAt();
    }

    public static RoomSummaryResponse of(Room room, ChatMessage lastMsg, long unreadCount) {
        String content = lastMsg != null ? lastMsg.getContent() : null;
        Instant msgAt = lastMsg != null ? lastMsg.getCreatedAt() : null;
        return new RoomSummaryResponse(room, content, msgAt, unreadCount);
    }
}
