package com.neocompany.taroro.domain.masterauth.entity;

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
@Table(name = "master_settlement")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterSettlement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settlementId;

    @Column(nullable = false, unique = true)
    private Long masterId;

    @Column(nullable = false, length = 100)
    private String bankName;

    @Column(nullable = false, length = 50)
    private String accountNumber;

    @Column(nullable = false, length = 100)
    private String accountHolderName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private boolean isVerifiedAccount = false;

    // ── 도메인 메서드 ─────────────────────────────────────────────────────────

    public void update(String bankName, String accountNumber, String accountHolderName,
                       String phone, String email) {
        if (bankName != null) this.bankName = bankName;
        boolean accountChanged = accountNumber != null && !accountNumber.equals(this.accountNumber);
        if (accountNumber != null) this.accountNumber = accountNumber;
        if (accountHolderName != null) this.accountHolderName = accountHolderName;
        if (phone != null) this.phone = phone;
        if (email != null) this.email = email;
        if (accountChanged) this.isVerifiedAccount = false;
    }
}
