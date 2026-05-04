package com.neocompany.taroro.domain.room.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.room.dto.event.CardEvent;
import com.neocompany.taroro.domain.room.dto.request.PickCardsRequest;
import com.neocompany.taroro.domain.room.dto.request.RevealCardRequest;
import com.neocompany.taroro.domain.room.dto.request.SelectCardSetRequest;
import com.neocompany.taroro.domain.room.dto.request.SpreadCardsRequest;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.entity.RoomCardReading;
import com.neocompany.taroro.domain.room.entity.RoomStatus;
import com.neocompany.taroro.domain.room.repository.RoomCardReadingRepository;
import com.neocompany.taroro.domain.room.repository.RoomRepository;
import com.neocompany.taroro.domain.tarocard.entity.TaroCard;
import com.neocompany.taroro.domain.tarocard.repository.TaroCardRepository;
import com.neocompany.taroro.domain.tarocardset.entity.TaroCardSet;
import com.neocompany.taroro.domain.tarocardset.repository.TaroCardSetRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CardCommandService {

    private final RoomRepository roomRepository;
    private final RoomCardReadingRepository cardReadingRepository;
    private final TaroCardSetRepository cardSetRepository;
    private final TaroCardRepository cardRepository;

    /** 마스터: 카드 세트 선택 — DB 저장 없이 브로드캐스트만 */
    public CardEvent selectCardSet(Long roomId, Long userId, SelectCardSetRequest request) {
        Room room = getRoom(roomId);
        requireMaster(room, userId);

        TaroCardSet set = cardSetRepository.findBySetIdAndDeletedFalse(request.getSetId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CARD_SET_NOT_FOUND));

        log.info("[Card] SET_SELECTED roomId={}, masterId={}, setId={}", roomId, userId, set.getSetId());
        return CardEvent.ofSetSelected(roomId, userId, set.getSetId(), set.getSetName());
    }

    /** 마스터: 카드 펼치기 — position별 DB 저장, cardId는 브로드캐스트에 미포함 */
    public CardEvent spread(Long roomId, Long userId, SpreadCardsRequest request) {
        Room room = getRoom(roomId);
        requireMaster(room, userId);
        requireActive(room);

        List<Long> cardIds = request.getCardIds();
        if (cardIds == null || cardIds.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "카드를 1장 이상 선택해야 합니다.");
        }

        // 중복 카드 검사
        if (cardIds.stream().distinct().count() != cardIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "중복된 카드가 있습니다.");
        }

        // 기존 리딩 초기화
        cardReadingRepository.deleteAllByRoomId(roomId);

        // position별 저장
        List<RoomCardReading> readings = IntStream.range(0, cardIds.size())
                .mapToObj(i -> RoomCardReading.create(roomId, request.getSetId(), cardIds.get(i), i))
                .toList();
        cardReadingRepository.saveAll(readings);

        List<CardEvent.SpreadCard> spreadCards = IntStream.range(0, cardIds.size())
                .mapToObj(CardEvent.SpreadCard::new)
                .toList();

        log.info("[Card] SPREAD roomId={}, masterId={}, count={}", roomId, userId, cardIds.size());
        return CardEvent.ofSpread(roomId, userId, spreadCards);
    }

    /** 유저: 카드 선택 */
    public CardEvent pick(Long roomId, Long userId, PickCardsRequest request) {
        Room room = getRoom(roomId);
        requireUser(room, userId);

        List<Integer> positions = request.getPositions();
        if (positions == null || positions.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "위치를 1개 이상 선택해야 합니다.");
        }

        List<RoomCardReading> readings = cardReadingRepository.findByRoomIdOrderByPosition(roomId);
        Set<Integer> validPositions = readings.stream()
                .map(RoomCardReading::getPosition)
                .collect(Collectors.toSet());

        for (Integer pos : positions) {
            if (!validPositions.contains(pos)) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "유효하지 않은 카드 위치입니다: " + pos);
            }
        }

        readings.stream()
                .filter(r -> positions.contains(r.getPosition()))
                .forEach(RoomCardReading::pick);

        log.info("[Card] PICKED roomId={}, userId={}, positions={}", roomId, userId, positions);
        return CardEvent.ofPicked(roomId, userId, positions);
    }

    /** 마스터: 카드 공개 — 카드 상세 정보를 이벤트에 포함 */
    public CardEvent reveal(Long roomId, Long userId, RevealCardRequest request) {
        Room room = getRoom(roomId);
        requireMaster(room, userId);

        RoomCardReading reading = cardReadingRepository
                .findByRoomIdAndPosition(roomId, request.getPosition())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REQUEST, "해당 위치의 카드가 없습니다."));

        TaroCard card = cardRepository.findByCardIdAndDeletedFalse(reading.getCardId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CARD_NOT_FOUND));

        reading.reveal();

        log.info("[Card] REVEALED roomId={}, masterId={}, position={}, cardId={}", roomId, userId, request.getPosition(), card.getCardId());
        return CardEvent.ofRevealed(roomId, userId, request.getPosition(), card);
    }

    /** 마스터: 리딩 초기화 */
    public CardEvent reset(Long roomId, Long userId) {
        Room room = getRoom(roomId);
        requireMaster(room, userId);

        cardReadingRepository.deleteAllByRoomId(roomId);

        log.info("[Card] RESET roomId={}, masterId={}", roomId, userId);
        return CardEvent.ofReset(roomId, userId);
    }

    // ── 공통 ──────────────────────────────────────────────────────────────────

    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
    }

    private void requireMaster(Room room, Long userId) {
        if (!room.isMaster(userId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED, "마스터만 수행할 수 있습니다.");
        }
    }

    private void requireUser(Room room, Long userId) {
        if (room.isMaster(userId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED, "유저만 카드를 선택할 수 있습니다.");
        }
        if (!room.isParticipant(userId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }
    }

    private void requireActive(Room room) {
        if (room.getStatus() != RoomStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.ROOM_INVALID_STATUS, "상담이 시작된 방에서만 카드를 펼칠 수 있습니다.");
        }
    }
}
