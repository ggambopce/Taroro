package com.neocompany.taroro.domain.message.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.message.dto.event.ChatMessageEvent;
import com.neocompany.taroro.domain.message.dto.request.SendMessageRequest;
import com.neocompany.taroro.domain.message.entity.ChatMessage;
import com.neocompany.taroro.domain.message.repository.ChatMessageRepository;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.entity.RoomStatus;
import com.neocompany.taroro.domain.room.repository.RoomRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MessageCommandService {

    private final ChatMessageRepository messageRepository;
    private final RoomRepository roomRepository;

    /**
     * 메시지 전송
     * - 참여자 검증
     * - CLOSED 방은 전송 불가
     * - DB 저장 후 이벤트 반환
     */
    public ChatMessageEvent send(Long roomId, Long senderId, SendMessageRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.isParticipant(senderId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }
        if (room.getStatus() == RoomStatus.CLOSED) {
            throw new BusinessException(ErrorCode.ROOM_INVALID_STATUS, "종료된 상담방에는 메시지를 보낼 수 없습니다.");
        }

        String content = request.getContent();
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.MESSAGE_INVALID);
        }

        ChatMessage saved = messageRepository.save(
                ChatMessage.of(roomId, senderId, content.trim(), request.getMessageType())
        );

        log.info("[Message] SEND roomId={}, senderId={}, messageId={}", roomId, senderId, saved.getId());
        return new ChatMessageEvent(saved);
    }
}