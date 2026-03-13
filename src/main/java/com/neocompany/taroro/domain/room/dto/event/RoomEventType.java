package com.neocompany.taroro.domain.room.dto.event;

public enum RoomEventType {
    ROOM_ENTER,  // 참여자 입장
    ROOM_LEAVE,  // 참여자 퇴장
    ROOM_START,  // 상담 시작 (WAITING → ACTIVE)
    ROOM_END     // 상담 종료 (ACTIVE → CLOSED)
}
