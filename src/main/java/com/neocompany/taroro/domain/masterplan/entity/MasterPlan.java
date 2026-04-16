package com.neocompany.taroro.domain.masterplan.entity;

import java.time.Instant;

import com.neocompany.taroro.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "master_plan")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterPlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @Column(nullable = false)
    private Long masterId;

    @Column(nullable = false, length = 200)
    private String planName;

    @Column(columnDefinition = "TEXT")
    private String planDescription;

    @Column(nullable = false)
    private Integer counselingMinutes;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    @Builder.Default
    private Integer discountRate = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean isPublic = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column
    private Instant deletedAt;

    // ── 도메인 메서드 ─────────────────────────────────────────────────────────

    public long getDiscountedPrice() {
        return price * (100 - discountRate) / 100;
    }

    public void update(String planName, String planDescription, Integer counselingMinutes,
                       Long price, Integer discountRate, Boolean isActive, Boolean isPublic) {
        if (planName != null) this.planName = planName;
        if (planDescription != null) this.planDescription = planDescription;
        if (counselingMinutes != null) this.counselingMinutes = counselingMinutes;
        if (price != null) this.price = price;
        if (discountRate != null) this.discountRate = discountRate;
        if (isActive != null) this.isActive = isActive;
        if (isPublic != null) this.isPublic = isPublic;
    }

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = Instant.now();
    }

    public boolean isOwnedBy(Long masterId) {
        return this.masterId.equals(masterId);
    }
}
