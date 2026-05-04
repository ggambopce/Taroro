package com.neocompany.taroro.domain.room.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.entity.RoomParticipant;
import com.neocompany.taroro.domain.room.entity.RoomStatus;

import lombok.Getter;

@Getter
public class RoomDetailResponse {

    private final Long id;
    private final Long masterId;
    private final String masterName;
    private final String roomName;
    private final RoomStatus status;
    private final Instant startedAt;
    private final Instant endedAt;
    private final Instant createdAt;
    private final List<ParticipantInfo> participants;

    private RoomDetailResponse(Room room, List<ParticipantInfo> participants) {
        this.id = room.getId();
        this.masterId = room.getMasterId();
        this.masterName = room.getMasterName();
        this.roomName = room.getRoomName();
        this.status = room.getStatus();
        this.startedAt = room.getStartedAt();
        this.endedAt = room.getEndedAt();
        this.createdAt = room.getCreatedAt();
        this.participants = participants;
    }

    public static RoomDetailResponse of(Room room, List<RoomParticipant> participants,
            Map<Long, String> userNameMap, Map<Long, Boolean> onlineMap) {
        List<ParticipantInfo> infos = participants.stream()
                .map(p -> {
                    boolean isOnline = Boolean.TRUE.equals(onlineMap.get(p.getUserId()));
                    return ParticipantInfo.of(p, userNameMap.getOrDefault(p.getUserId(), ""),
                            room.getMasterId(), isOnline);
                })
                .toList();
        return new RoomDetailResponse(room, infos);
    }
}
