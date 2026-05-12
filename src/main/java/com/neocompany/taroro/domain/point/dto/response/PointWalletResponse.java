package com.neocompany.taroro.domain.point.dto.response;

import com.neocompany.taroro.domain.point.entity.PointWallet;

import lombok.Getter;

@Getter
public class PointWalletResponse {

    private final Long userId;
    private final long balance;

    public PointWalletResponse(Long userId, long balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public static PointWalletResponse from(Long userId, PointWallet wallet) {
        return new PointWalletResponse(userId, wallet == null ? 0L : wallet.getBalance());
    }
}
