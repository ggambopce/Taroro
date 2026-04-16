package com.neocompany.taroro.domain.masterauth.dto.response;

import java.time.Instant;

import com.neocompany.taroro.domain.masterauth.entity.MasterVerification;

import lombok.Getter;

@Getter
public class VerificationResponse {

    private final Long verificationId;
    private final Long masterId;
    private final boolean isIdentityVerified;
    private final boolean isPassVerified;
    private final String verificationStatus;
    private final Instant passVerifiedAt;
    private final Instant identityVerifiedAt;
    private final String rejectReason;
    private final Instant createdAt;
    private final Instant updatedAt;

    public VerificationResponse(MasterVerification verification) {
        this.verificationId = verification.getVerificationId();
        this.masterId = verification.getMasterId();
        this.isIdentityVerified = verification.isIdentityVerified();
        this.isPassVerified = verification.isPassVerified();
        this.verificationStatus = verification.getVerificationStatus();
        this.passVerifiedAt = verification.getPassVerifiedAt();
        this.identityVerifiedAt = verification.getIdentityVerifiedAt();
        this.rejectReason = verification.getRejectReason();
        this.createdAt = verification.getCreatedAt();
        this.updatedAt = verification.getUpdatedAt();
    }
}
