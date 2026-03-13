package com.neocompany.taroro.domain.message.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

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
 * taroro API 서버의 message 테이블 공유 매핑
 * ※ 실제 테이블명/컬럼명이 다를 경우 수정 필요 (예: chat_message)
 */
@Entity
@Table(name = "message")
@Getter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", length = 20)
    private MessageType messageType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // ── 팩토리 메서드 ─────────────────────────────────────────────────────────

    public static ChatMessage of(Long roomId, Long senderId, String content, MessageType messageType) {
        ChatMessage m = new ChatMessage();
        m.roomId = roomId;
        m.senderId = senderId;
        m.content = content;
        m.messageType = messageType;
        return m;
    }
}
