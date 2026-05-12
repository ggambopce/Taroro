package com.neocompany.taroro.domain.payment.dto.response;

import com.neocompany.taroro.domain.payment.entity.MasterEarningWallet;

import lombok.Getter;

@Getter
public class MasterEarningWalletResponse {

    private final Long masterId;
    private final long balance;
    private final long totalEarned;
    private final long totalWithdrawn;

    public MasterEarningWalletResponse(Long masterId, long balance, long totalEarned, long totalWithdrawn) {
        this.masterId = masterId;
        this.balance = balance;
        this.totalEarned = totalEarned;
        this.totalWithdrawn = totalWithdrawn;
    }

    public static MasterEarningWalletResponse from(Long masterId, MasterEarningWallet w) {
        if (w == null) return new MasterEarningWalletResponse(masterId, 0L, 0L, 0L);
        return new MasterEarningWalletResponse(masterId, w.getBalance(), w.getTotalEarned(), w.getTotalWithdrawn());
    }
}
