package com.neocompany.taroro.domain.masterauth.entity;

import java.time.Instant;

import com.neocompany.taroro.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "master_verification")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterVerification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long verificationId;

    @Column(nullable = false, unique = true)
    private Long masterId;

    @Column(nullable = false)
    @Builder.Default
    private boolean isIdentityVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isPassVerified = false;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String verificationStatus = "NONE";

    @Column
    private Instant passVerifiedAt;

    @Column
    private Instant identityVerifiedAt;

    @Column(columnDefinition = "TEXT")
    private String rejectReason;

    // ── 도메인 메서드 ─────────────────────────────────────────────────────────

    public void completePassVerification() {
        this.isPassVerified = true;
        this.passVerifiedAt = Instant.now();
        this.verificationStatus = "VERIFIED";
    }

    public void failPassVerification(String reason) {
        this.isPassVerified = false;
        this.verificationStatus = "REJECTED";
        this.rejectReason = reason;
    }
}
