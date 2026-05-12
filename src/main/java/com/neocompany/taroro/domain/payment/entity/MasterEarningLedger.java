package com.neocompany.taroro.domain.payment.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.neocompany.taroro.domain.payment.enums.MasterEarningType;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "master_earning_ledger")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterEarningLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private TaroMaster master;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private MasterEarningWallet wallet;

    @Column(nullable = false)
    private long delta;

    @Column(nullable = false)
    private long balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MasterEarningType type;

    @Column(length = 50)
    private String refTable;

    private Long refId;

    @CreationTimestamp
    private Instant createdAt;

    public static MasterEarningLedger of(MasterEarningWallet wallet, long delta,
                                          MasterEarningType type, String refTable, Long refId) {
        return MasterEarningLedger.builder()
                .master(wallet.getMaster())
                .wallet(wallet)
                .delta(delta)
                .balanceAfter(wallet.getBalance())
                .type(type)
                .refTable(refTable)
                .refId(refId)
                .build();
    }
}
