package com.neocompany.taroro.domain.payment.dto.response;

import java.time.Instant;

import com.neocompany.taroro.domain.payment.entity.MasterEarningLedger;
import com.neocompany.taroro.domain.payment.enums.MasterEarningType;

import lombok.Getter;

@Getter
public class MasterEarningLedgerResponse {

    private final Long id;
    private final long delta;
    private final long balanceAfter;
    private final MasterEarningType type;
    private final String refTable;
    private final Long refId;
    private final Instant createdAt;

    public MasterEarningLedgerResponse(MasterEarningLedger l) {
        this.id = l.getId();
        this.delta = l.getDelta();
        this.balanceAfter = l.getBalanceAfter();
        this.type = l.getType();
        this.refTable = l.getRefTable();
        this.refId = l.getRefId();
        this.createdAt = l.getCreatedAt();
    }
}
