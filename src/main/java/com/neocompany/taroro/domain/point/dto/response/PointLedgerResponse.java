package com.neocompany.taroro.domain.point.dto.response;

import java.time.Instant;

import com.neocompany.taroro.domain.point.entity.PointLedger;
import com.neocompany.taroro.domain.point.enums.PointLedgerType;

import lombok.Getter;

@Getter
public class PointLedgerResponse {

    private final Long id;
    private final long delta;
    private final long balanceAfter;
    private final PointLedgerType type;
    private final String refTable;
    private final Long refId;
    private final Instant createdAt;

    public PointLedgerResponse(PointLedger l) {
        this.id = l.getId();
        this.delta = l.getDelta();
        this.balanceAfter = l.getBalanceAfter();
        this.type = l.getType();
        this.refTable = l.getRefTable();
        this.refId = l.getRefId();
        this.createdAt = l.getCreatedAt();
    }
}
