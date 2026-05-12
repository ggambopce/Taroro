package com.neocompany.taroro.domain.payment.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.neocompany.taroro.domain.payment.enums.PaymentStatus;
import com.neocompany.taroro.domain.room.entity.Room;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "consultation_payment")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, unique = true)
    private Room room;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long masterId;

    @Column(nullable = false)
    private Long planId;

    @Column(nullable = false)
    private long grossAmount;

    @Column(nullable = false)
    private long platformFee;

    @Column(nullable = false)
    private long netToMaster;

    @Column(nullable = false)
    private int appliedCommissionRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false)
    private Instant paidAt;

    private Instant refundedAt;

    @CreationTimestamp
    private Instant createdAt;

    public static ConsultationPayment completed(Room room, Long userId, Long masterId, Long planId,
                                                 long grossAmount, long platformFee, long netToMaster,
                                                 int appliedCommissionRate) {
        return ConsultationPayment.builder()
                .room(room)
                .userId(userId)
                .masterId(masterId)
                .planId(planId)
                .grossAmount(grossAmount)
                .platformFee(platformFee)
                .netToMaster(netToMaster)
                .appliedCommissionRate(appliedCommissionRate)
                .status(PaymentStatus.COMPLETED)
                .paidAt(Instant.now())
                .build();
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
        this.refundedAt = Instant.now();
    }
}
