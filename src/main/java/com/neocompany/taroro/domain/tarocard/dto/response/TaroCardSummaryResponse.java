package com.neocompany.taroro.domain.tarocard.dto.response;

import com.neocompany.taroro.domain.tarocard.entity.ArcanaType;
import com.neocompany.taroro.domain.tarocard.entity.SuitType;
import com.neocompany.taroro.domain.tarocard.entity.TaroCard;

import lombok.Getter;

@Getter
public class TaroCardSummaryResponse {

    private final Long cardId;
    private final String cardName;
    private final Integer cardNumber;
    private final ArcanaType arcanaType;
    private final SuitType suit;
    private final String imageUrl;
    private final boolean isActive;

    public TaroCardSummaryResponse(TaroCard card) {
        this.cardId = card.getCardId();
        this.cardName = card.getCardName();
        this.cardNumber = card.getCardNumber();
        this.arcanaType = card.getArcanaType();
        this.suit = card.getSuit();
        this.imageUrl = card.getImageUrl();
        this.isActive = card.isActive();
    }
}
