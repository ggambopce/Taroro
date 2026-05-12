package com.neocompany.taroro.domain.payment.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "master_earning_wallet")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterEarningWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false, unique = true)
    private TaroMaster master;

    @Column(nullable = false)
    private long balance;

    @Column(nullable = false)
    private long totalEarned;

    @Column(nullable = false)
    private long totalWithdrawn;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public static MasterEarningWallet empty(TaroMaster master) {
        return MasterEarningWallet.builder()
                .master(master)
                .balance(0L)
                .totalEarned(0L)
                .totalWithdrawn(0L)
                .build();
    }

    public void credit(long amount) {
        this.balance += amount;
        this.totalEarned += amount;
    }

    public void deduct(long amount) {
        this.balance -= amount;
    }

    public void lockForWithdrawal(long amount) {
        this.balance -= amount;
        this.totalWithdrawn += amount;
    }

    public void restoreLocked(long amount) {
        this.balance += amount;
        this.totalWithdrawn -= amount;
    }
}
