package com.neocompany.taroro.domain.masterauth.dto.response;

import java.time.Instant;

import com.neocompany.taroro.domain.masterauth.entity.MasterSettlement;

import lombok.Getter;

@Getter
public class SettlementResponse {

    private final Long settlementId;
    private final Long masterId;
    private final String bankName;
    private final String accountNumber;
    private final String accountHolderName;
    private final String phone;
    private final String email;
    private final boolean isVerifiedAccount;
    private final Instant createdAt;
    private final Instant updatedAt;

    public SettlementResponse(MasterSettlement settlement) {
        this.settlementId = settlement.getSettlementId();
        this.masterId = settlement.getMasterId();
        this.bankName = settlement.getBankName();
        this.accountNumber = settlement.getAccountNumber();
        this.accountHolderName = settlement.getAccountHolderName();
        this.phone = settlement.getPhone();
        this.email = settlement.getEmail();
        this.isVerifiedAccount = settlement.isVerifiedAccount();
        this.createdAt = settlement.getCreatedAt();
        this.updatedAt = settlement.getUpdatedAt();
    }
}
