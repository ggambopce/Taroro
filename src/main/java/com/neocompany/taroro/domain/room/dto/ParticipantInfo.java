package com.neocompany.taroro.domain.room.dto;

import com.neocompany.taroro.domain.room.entity.RoomParticipant;

public record ParticipantInfo(Long userId, String userName, String role, boolean isOnline) {

    public static ParticipantInfo of(RoomParticipant p, String userName, Long masterId, boolean isOnline) {
        String role = p.getUserId().equals(masterId) ? "MASTER" : "USER";
        return new ParticipantInfo(p.getUserId(), userName, role, isOnline);
    }
}
