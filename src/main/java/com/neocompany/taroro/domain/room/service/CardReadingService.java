package com.neocompany.taroro.domain.room.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.room.dto.CardReadingResponse;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.entity.RoomCardReading;
import com.neocompany.taroro.domain.room.repository.RoomCardReadingRepository;
import com.neocompany.taroro.domain.room.repository.RoomRepository;
import com.neocompany.taroro.domain.tarocard.entity.TaroCard;
import com.neocompany.taroro.domain.tarocard.repository.TaroCardRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardReadingService {

    private final RoomRepository roomRepository;
    private final RoomCardReadingRepository cardReadingRepository;
    private final TaroCardRepository cardRepository;

    public CardReadingResponse getReading(Long roomId, Long requesterId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.isMaster(requesterId) && !room.isParticipant(requesterId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }

        List<RoomCardReading> readings = cardReadingRepository.findByRoomIdOrderByPosition(roomId);

        Set<Long> revealedCardIds = readings.stream()
                .filter(RoomCardReading::isRevealed)
                .map(RoomCardReading::getCardId)
                .collect(Collectors.toSet());

        Map<Long, TaroCard> cardMap = cardRepository.findByCardIdInAndDeletedFalse(revealedCardIds)
                .stream()
                .collect(Collectors.toMap(TaroCard::getCardId, c -> c));

        List<CardReadingResponse.CardItem> items = readings.stream()
                .map(r -> {
                    if (r.isRevealed()) {
                        TaroCard card = cardMap.get(r.getCardId());
                        return card != null
                                ? CardReadingResponse.CardItem.revealed(r, card)
                                : CardReadingResponse.CardItem.hidden(r);
                    }
                    return CardReadingResponse.CardItem.hidden(r);
                })
                .toList();

        return new CardReadingResponse(items);
    }
}
