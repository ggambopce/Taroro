package com.neocompany.taroro.domain.point.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.neocompany.taroro.domain.point.enums.PointLedgerType;
import com.neocompany.taroro.domain.users.User;

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
@Table(name = "point_ledger")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private PointWallet wallet;

    @Column(nullable = false)
    private long delta;

    @Column(nullable = false)
    private long balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PointLedgerType type;

    @Column(length = 50)
    private String refTable;

    private Long refId;

    @CreationTimestamp
    private Instant createdAt;
}
