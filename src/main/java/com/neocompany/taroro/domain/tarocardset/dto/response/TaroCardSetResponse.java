package com.neocompany.taroro.domain.tarocardset.dto.response;

import java.time.Instant;

import com.neocompany.taroro.domain.tarocardset.entity.TaroCardSet;

import lombok.Getter;

@Getter
public class TaroCardSetResponse {

    private final Long setId;
    private final Long masterId;
    private String masterName;
    private final String setName;
    private final String setDescription;
    private final String brandName;
    private final String publisherName;
    private final String coverImageUrl;
    private final Integer cardCount;
    private final boolean isActive;
    private final boolean isPublic;
    private final Instant createdAt;
    private final Instant updatedAt;

    public TaroCardSetResponse(TaroCardSet set) {
        this.setId = set.getSetId();
        this.masterId = set.getMasterId();
        this.setName = set.getSetName();
        this.setDescription = set.getSetDescription();
        this.brandName = set.getBrandName();
        this.publisherName = set.getPublisherName();
        this.coverImageUrl = set.getCoverImageUrl();
        this.cardCount = set.getCardCount();
        this.isActive = set.isActive();
        this.isPublic = set.isPublic();
        this.createdAt = set.getCreatedAt();
        this.updatedAt = set.getUpdatedAt();
    }

    public TaroCardSetResponse withMasterName(String masterName) {
        this.masterName = masterName;
        return this;
    }
}
