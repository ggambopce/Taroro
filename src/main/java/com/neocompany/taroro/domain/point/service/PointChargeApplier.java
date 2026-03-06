package com.neocompany.taroro.domain.point.service;

import java.time.Instant;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.point.entity.PointCharge;
import com.neocompany.taroro.domain.point.entity.PointLedger;
import com.neocompany.taroro.domain.point.entity.PointWallet;
import com.neocompany.taroro.domain.point.enums.PointChargeStatus;
import com.neocompany.taroro.domain.point.enums.PointLedgerType;
import com.neocompany.taroro.domain.point.repository.PointChargeRepository;
import com.neocompany.taroro.domain.point.repository.PointLedgerRepository;
import com.neocompany.taroro.domain.point.repository.PointWalletRepository;
import com.neocompany.taroro.domain.users.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointChargeApplier {

    private final PointChargeRepository pointChargeRepository;
    private final PointWalletRepository pointWalletRepository;
    private final PointLedgerRepository pointLedgerRepository;

    @Transactional
    public void apply(PointCharge charge, String paymentKey, Instant approvedAt) {
        // 1. Update point_charge
        charge.setPaymentKey(paymentKey);
        charge.setApprovedAt(approvedAt);
        charge.setStatus(PointChargeStatus.PAID);
        pointChargeRepository.save(charge);

        // 2. Get or create wallet
        User user = charge.getUser();
        PointWallet wallet = pointWalletRepository.findByUser(user)
                .orElseGet(() -> pointWalletRepository.save(
                        PointWallet.builder().user(user).balance(0L).build()
                ));

        // 3. Update wallet balance
        long newBalance = wallet.getBalance() + charge.getAmount();
        wallet.setBalance(newBalance);
        pointWalletRepository.save(wallet);

        // 4. Create ledger entry
        pointLedgerRepository.save(PointLedger.builder()
                .user(user)
                .wallet(wallet)
                .delta(charge.getAmount())
                .balanceAfter(newBalance)
                .type(PointLedgerType.CHARGE)
                .refTable("point_charge")
                .refId(charge.getId())
                .build());
    }
}
