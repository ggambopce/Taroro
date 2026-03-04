package com.neocompany.taroro.global.sessions;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "session",
       indexes = {
           @Index(name = "idx_sessions_user_id", columnList = "user_id"),
           @Index(name = "idx_sessions_expires_at", columnList = "expires_at")
       })
@Getter @Setter
public class Session {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="session_id", nullable=false, unique=true, length=128)
    private String sessionId; // 쿠키에 담길 토큰

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="expires_at", nullable=false)
    private Instant expiresAt;

    @Column(name="last_access_at")
    private Instant lastAccessAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
