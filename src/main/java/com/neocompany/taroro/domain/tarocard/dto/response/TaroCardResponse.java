package com.neocompany.taroro.domain.tarocard.dto.response;

import java.time.Instant;
import java.util.List;

import com.neocompany.taroro.domain.tarocard.entity.ArcanaType;
import com.neocompany.taroro.domain.tarocard.entity.SuitType;
import com.neocompany.taroro.domain.tarocard.entity.TaroCard;

import lombok.Getter;

@Getter
public class TaroCardResponse {

    private final Long cardId;
    private final Long setId;
    private final Long masterId;
    private final String cardName;
    private final Integer cardNumber;
    private final ArcanaType arcanaType;
    private final SuitType suit;
    private final List<String> keywords;
    private final String cardDescription;
    private final String uprightMeaning;
    private final String reversedMeaning;
    private final String imageUrl;
    private final boolean isActive;
    private final Instant createdAt;
    private final Instant updatedAt;

    public TaroCardResponse(TaroCard card) {
        this.cardId = card.getCardId();
        this.setId = card.getSetId();
        this.masterId = card.getMasterId();
        this.cardName = card.getCardName();
        this.cardNumber = card.getCardNumber();
        this.arcanaType = card.getArcanaType();
        this.suit = card.getSuit();
        this.keywords = card.getKeywords();
        this.cardDescription = card.getCardDescription();
        this.uprightMeaning = card.getUprightMeaning();
        this.reversedMeaning = card.getReversedMeaning();
        this.imageUrl = card.getImageUrl();
        this.isActive = card.isActive();
        this.createdAt = card.getCreatedAt();
        this.updatedAt = card.getUpdatedAt();
    }
}
