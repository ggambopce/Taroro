package com.neocompany.taroro.domain.room.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neocompany.taroro.domain.room.entity.RoomCardReading;
import com.neocompany.taroro.domain.tarocard.entity.ArcanaType;
import com.neocompany.taroro.domain.tarocard.entity.SuitType;
import com.neocompany.taroro.domain.tarocard.entity.TaroCard;

import lombok.Getter;

@Getter
public class CardReadingResponse {

    private final List<CardItem> cards;

    public CardReadingResponse(List<CardItem> cards) {
        this.cards = cards;
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CardItem {
        private final int position;
        private final boolean isPicked;
        private final boolean isRevealed;

        // 공개된 카드만 포함
        private final Long cardId;
        private final String cardName;
        private final ArcanaType arcanaType;
        private final SuitType suit;
        private final List<String> keywords;
        private final String imageUrl;
        private final String uprightMeaning;
        private final String reversedMeaning;

        public static CardItem hidden(RoomCardReading r) {
            return new CardItem(r.getPosition(), r.isPicked(), false,
                    null, null, null, null, null, null, null, null);
        }

        public static CardItem revealed(RoomCardReading r, TaroCard card) {
            return new CardItem(r.getPosition(), r.isPicked(), true,
                    card.getCardId(), card.getCardName(), card.getArcanaType(), card.getSuit(),
                    card.getKeywords(), card.getImageUrl(), card.getUprightMeaning(), card.getReversedMeaning());
        }

        private CardItem(int position, boolean isPicked, boolean isRevealed,
                Long cardId, String cardName, ArcanaType arcanaType, SuitType suit,
                List<String> keywords, String imageUrl, String uprightMeaning, String reversedMeaning) {
            this.position = position;
            this.isPicked = isPicked;
            this.isRevealed = isRevealed;
            this.cardId = cardId;
            this.cardName = cardName;
            this.arcanaType = arcanaType;
            this.suit = suit;
            this.keywords = keywords;
            this.imageUrl = imageUrl;
            this.uprightMeaning = uprightMeaning;
            this.reversedMeaning = reversedMeaning;
        }
    }
}
