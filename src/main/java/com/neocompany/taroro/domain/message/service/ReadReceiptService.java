package com.neocompany.taroro.domain.message.service;

import org.springframework.stereotype.Service;

import com.neocompany.taroro.domain.message.dto.event.ReadUpdatedEvent;
import com.neocompany.taroro.domain.message.entity.MessageRead;
import com.neocompany.taroro.domain.message.repository.MessageReadRepository;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.repository.RoomRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReadReceiptService {

    private final MessageReadRepository messageReadRepository;
    private final RoomRepository roomRepository;

    /**
     * 읽음 처리 — 없으면 INSERT, 있으면 UPDATE
     */
    public ReadUpdatedEvent updateReadReceipt(Long roomId, Long userId, Long lastReadMessageId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.isParticipant(userId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }

        messageReadRepository.findByRoomIdAndUserId(roomId, userId)
                .ifPresentOrElse(
                        read -> read.update(lastReadMessageId),
                        () -> messageReadRepository.save(MessageRead.create(roomId, userId, lastReadMessageId))
                );

        log.debug("[ReadReceipt] roomId={}, userId={}, lastReadId={}", roomId, userId, lastReadMessageId);
        return new ReadUpdatedEvent(roomId, userId, lastReadMessageId);
    }
}

