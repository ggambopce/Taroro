package com.neocompany.taroro.domain.masterplan.dto.response;

import java.time.Instant;

import com.neocompany.taroro.domain.masterplan.entity.MasterPlan;

import lombok.Getter;

@Getter
public class MasterPlanResponse {

    private final Long planId;
    private final Long masterId;
    private final String planName;
    private final String planDescription;
    private final Integer counselingMinutes;
    private final Long price;
    private final Integer discountRate;
    private final Long discountedPrice;
    private final boolean isActive;
    private final boolean isPublic;
    private final Instant createdAt;
    private final Instant updatedAt;

    public MasterPlanResponse(MasterPlan plan) {
        this.planId = plan.getPlanId();
        this.masterId = plan.getMasterId();
        this.planName = plan.getPlanName();
        this.planDescription = plan.getPlanDescription();
        this.counselingMinutes = plan.getCounselingMinutes();
        this.price = plan.getPrice();
        this.discountRate = plan.getDiscountRate();
        this.discountedPrice = plan.getDiscountedPrice();
        this.isActive = plan.isActive();
        this.isPublic = plan.isPublic();
        this.createdAt = plan.getCreatedAt();
        this.updatedAt = plan.getUpdatedAt();
    }
}
