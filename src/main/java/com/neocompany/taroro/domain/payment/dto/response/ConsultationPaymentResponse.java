package com.neocompany.taroro.domain.payment.dto.response;

import java.time.Instant;

import com.neocompany.taroro.domain.payment.entity.ConsultationPayment;
import com.neocompany.taroro.domain.payment.enums.PaymentStatus;

import lombok.Getter;

@Getter
public class ConsultationPaymentResponse {

    private final Long id;
    private final Long roomId;
    private final Long userId;
    private final Long masterId;
    private final Long planId;
    private final long grossAmount;
    private final long platformFee;
    private final long netToMaster;
    private final int appliedCommissionRate;
    private final PaymentStatus status;
    private final Instant paidAt;
    private final Instant refundedAt;

    public ConsultationPaymentResponse(ConsultationPayment p) {
        this.id = p.getId();
        this.roomId = p.getRoom().getId();
        this.userId = p.getUserId();
        this.masterId = p.getMasterId();
        this.planId = p.getPlanId();
        this.grossAmount = p.getGrossAmount();
        this.platformFee = p.getPlatformFee();
        this.netToMaster = p.getNetToMaster();
        this.appliedCommissionRate = p.getAppliedCommissionRate();
        this.status = p.getStatus();
        this.paidAt = p.getPaidAt();
        this.refundedAt = p.getRefundedAt();
    }
}
