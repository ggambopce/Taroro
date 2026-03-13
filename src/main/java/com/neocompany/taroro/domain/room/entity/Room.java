package com.neocompany.taroro.domain.room.entity;

import java.time.Instant;

import com.neocompany.taroro.global.entity.BaseTimeEntity;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * taroro API 서버의 room 테이블 공유 매핑
 * ※ 실제 테이블 스키마가 다를 경우 필드명/컬럼명 수정 필요
 */
@Entity
@Table(name = "room")
@Getter
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "master_id")
    private Long masterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    // ── 도메인 메서드 ─────────────────────────────────────────────────────────

    /** 상담 시작 — WAITING → ACTIVE (마스터 전용) */
    public void start() {
        if (this.status != RoomStatus.WAITING) {
            throw new BusinessException(ErrorCode.ROOM_INVALID_STATUS);
        }
        this.status = RoomStatus.ACTIVE;
        this.startedAt = Instant.now();
    }

    /** 상담 종료 — ACTIVE → CLOSED (마스터 전용) */
    public void end() {
        if (this.status != RoomStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.ROOM_INVALID_STATUS);
        }
        this.status = RoomStatus.CLOSED;
        this.endedAt = Instant.now();
    }

    public boolean isParticipant(Long userId) {
        return userId.equals(this.userId) || userId.equals(this.masterId);
    }

    public boolean isMaster(Long userId) {
        return userId.equals(this.masterId);
    }
}