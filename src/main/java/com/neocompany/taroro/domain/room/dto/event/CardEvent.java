package com.neocompany.taroro.domain.room.dto.event;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neocompany.taroro.domain.tarocard.entity.ArcanaType;
import com.neocompany.taroro.domain.tarocard.entity.SuitType;
import com.neocompany.taroro.domain.tarocard.entity.TaroCard;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardEvent {

    private final String eventType;
    private final Long roomId;
    private final Long triggeredBy;
    private final Instant timestamp;

    // CARD_SET_SELECTED
    private final Long setId;
    private final String setName;

    // CARDS_SPREAD
    private final Integer spreadCount;
    private final List<SpreadCard> cards;

    // CARD_PICKED
    private final List<Integer> positions;

    // CARD_REVEALED
    private final Integer position;
    private final Long cardId;
    private final String cardName;
    private final ArcanaType arcanaType;
    private final SuitType suit;
    private final List<String> keywords;
    private final String imageUrl;
    private final String uprightMeaning;
    private final String reversedMeaning;

    @Getter
    public static class SpreadCard {
        private final int position;

        public SpreadCard(int position) {
            this.position = position;
        }
    }

    public static CardEvent ofSetSelected(Long roomId, Long triggeredBy, Long setId, String setName) {
        return CardEvent.builder()
                .eventType(CardEventType.CARD_SET_SELECTED.name())
                .roomId(roomId).triggeredBy(triggeredBy).timestamp(Instant.now())
                .setId(setId).setName(setName)
                .build();
    }

    public static CardEvent ofSpread(Long roomId, Long triggeredBy, List<SpreadCard> cards) {
        return CardEvent.builder()
                .eventType(CardEventType.CARDS_SPREAD.name())
                .roomId(roomId).triggeredBy(triggeredBy).timestamp(Instant.now())
                .spreadCount(cards.size()).cards(cards)
                .build();
    }

    public static CardEvent ofPicked(Long roomId, Long triggeredBy, List<Integer> positions) {
        return CardEvent.builder()
                .eventType(CardEventType.CARD_PICKED.name())
                .roomId(roomId).triggeredBy(triggeredBy).timestamp(Instant.now())
                .positions(positions)
                .build();
    }

    public static CardEvent ofRevealed(Long roomId, Long triggeredBy, int position, TaroCard card) {
        return CardEvent.builder()
                .eventType(CardEventType.CARD_REVEALED.name())
                .roomId(roomId).triggeredBy(triggeredBy).timestamp(Instant.now())
                .position(position)
                .cardId(card.getCardId())
                .cardName(card.getCardName())
                .arcanaType(card.getArcanaType())
                .suit(card.getSuit())
                .keywords(card.getKeywords())
                .imageUrl(card.getImageUrl())
                .uprightMeaning(card.getUprightMeaning())
                .reversedMeaning(card.getReversedMeaning())
                .build();
    }

    public static CardEvent ofReset(Long roomId, Long triggeredBy) {
        return CardEvent.builder()
                .eventType(CardEventType.READING_RESET.name())
                .roomId(roomId).triggeredBy(triggeredBy).timestamp(Instant.now())
                .build();
    }
}
