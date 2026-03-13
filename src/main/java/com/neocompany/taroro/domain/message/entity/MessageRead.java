package com.neocompany.taroro.domain.message.entity;

import java.time.Instant;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

/**
 * 유저별 마지막 읽은 메시지 위치 (WebSocket 서버 전용 테이블)
 * room_id + user_id 유니크 — 없으면 INSERT, 있으면 UPDATE
 */
@Entity
@Table(name = "message_read",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_message_read_room_user",
                columnNames = {"room_id", "user_id"}))
@Getter
public class MessageRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "last_read_message_id", nullable = false)
    private Long lastReadMessageId;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // ── 팩토리 / 도메인 메서드 ─────────────────────────────────────────────────

    public static MessageRead create(Long roomId, Long userId, Long lastReadMessageId) {
        MessageRead r = new MessageRead();
        r.roomId = roomId;
        r.userId = userId;
        r.lastReadMessageId = lastReadMessageId;
        return r;
    }

    public void update(Long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }
}
