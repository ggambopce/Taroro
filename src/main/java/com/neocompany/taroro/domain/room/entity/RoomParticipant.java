package com.neocompany.taroro.domain.room.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * 방 실시간 참여 현황 추적 (WebSocket 서버 전용 테이블)
 * enter 시 생성, leave 시 left_at 업데이트
 */
@Entity
@Table(name = "room_participant",
        indexes = @Index(name = "idx_room_participant_room_user", columnList = "room_id, user_id"))
@Getter
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;

    // ── 팩토리 메서드 ─────────────────────────────────────────────────────────

    public static RoomParticipant join(Long roomId, Long userId) {
        RoomParticipant p = new RoomParticipant();
        p.roomId = roomId;
        p.userId = userId;
        return p;
    }

    // ── 도메인 메서드 ─────────────────────────────────────────────────────────

    public void leave() {
        this.leftAt = Instant.now();
    }

    public boolean isOnline() {
        return this.leftAt == null;
    }
}