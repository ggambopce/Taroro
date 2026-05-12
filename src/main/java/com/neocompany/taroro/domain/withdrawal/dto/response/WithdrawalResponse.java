package com.neocompany.taroro.domain.withdrawal.dto.response;

import java.time.Instant;

import com.neocompany.taroro.domain.withdrawal.entity.WithdrawalRequest;
import com.neocompany.taroro.domain.withdrawal.enums.WithdrawalStatus;

import lombok.Getter;

@Getter
public class WithdrawalResponse {

    private final Long id;
    private final Long masterId;
    private final long amount;
    private final String bankName;
    private final String accountNumber;
    private final String accountHolderName;
    private final WithdrawalStatus status;
    private final Long processedByAdminId;
    private final Instant requestedAt;
    private final Instant processedAt;
    private final String rejectReason;

    public WithdrawalResponse(WithdrawalRequest w) {
        this.id = w.getId();
        this.masterId = w.getMaster().getMasterId();
        this.amount = w.getAmount();
        this.bankName = w.getBankName();
        this.accountNumber = w.getAccountNumber();
        this.accountHolderName = w.getAccountHolderName();
        this.status = w.getStatus();
        this.processedByAdminId = w.getProcessedByAdminId();
        this.requestedAt = w.getRequestedAt();
        this.processedAt = w.getProcessedAt();
        this.rejectReason = w.getRejectReason();
    }
}
