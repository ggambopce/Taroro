package com.neocompany.taroro.domain.message.service;

import org.springframework.stereotype.Service;

import com.neocompany.taroro.domain.message.dto.event.TypingEvent;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.repository.RoomRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TypingEventService {

    private final RoomRepository roomRepository;

    /**
     * 타이핑 이벤트 — DB 저장 없이 이벤트만 생성
     */
    public TypingEvent typing(Long roomId, Long userId, boolean isTyping) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.isParticipant(userId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }

        return new TypingEvent(roomId, userId, isTyping);
    }
}
