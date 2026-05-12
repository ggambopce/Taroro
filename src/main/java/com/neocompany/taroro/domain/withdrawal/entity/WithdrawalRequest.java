package com.neocompany.taroro.domain.withdrawal.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.withdrawal.enums.WithdrawalStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "withdrawal_request")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private TaroMaster master;

    @Column(nullable = false)
    private long amount;

    // 신청 시점의 계좌 스냅샷 — 이후 정산 계좌 변경 영향 없음
    @Column(nullable = false, length = 100)
    private String bankName;

    @Column(nullable = false, length = 50)
    private String accountNumber;

    @Column(nullable = false, length = 100)
    private String accountHolderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WithdrawalStatus status;

    private Long processedByAdminId;

    @CreationTimestamp
    private Instant requestedAt;

    private Instant processedAt;

    @Lob
    private String rejectReason;

    @UpdateTimestamp
    private Instant updatedAt;

    public static WithdrawalRequest create(TaroMaster master, long amount,
                                            String bankName, String accountNumber, String accountHolderName) {
        return WithdrawalRequest.builder()
                .master(master)
                .amount(amount)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .status(WithdrawalStatus.PENDING)
                .build();
    }

    public void approve(Long adminUserId) {
        this.status = WithdrawalStatus.APPROVED;
        this.processedByAdminId = adminUserId;
        this.processedAt = Instant.now();
    }

    public void reject(Long adminUserId, String reason) {
        this.status = WithdrawalStatus.REJECTED;
        this.processedByAdminId = adminUserId;
        this.processedAt = Instant.now();
        this.rejectReason = reason;
    }

    public void complete() {
        this.status = WithdrawalStatus.COMPLETED;
        this.processedAt = Instant.now();
    }
}
