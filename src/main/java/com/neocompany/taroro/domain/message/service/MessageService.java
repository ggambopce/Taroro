package com.neocompany.taroro.domain.message.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.message.dto.response.ChatMessageResponse;
import com.neocompany.taroro.domain.message.repository.ChatMessageRepository;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.repository.RoomRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ChatMessageRepository messageRepository;
    private final RoomRepository roomRepository;

    /**
     * 방 메시지 목록 조회 (커서 기반 페이징)
     *
     * @param cursorId null 이면 최신부터, 값이 있으면 해당 id 이전 메시지
     */
    public ChatMessageResponse.PageResult getMessages(Long roomId, Long requesterId, Long cursorId, int size) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        if (!isParticipant(room, requesterId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }

        int pageSize = (size > 0 && size <= 100) ? size : DEFAULT_PAGE_SIZE;
        PageRequest pageRequest = PageRequest.of(0, pageSize);

        Slice<ChatMessageResponse> slice;
        if (cursorId == null) {
            slice = messageRepository.findByRoomIdOrderByIdDesc(roomId, pageRequest)
                    .map(ChatMessageResponse::new);
        } else {
            slice = messageRepository.findByRoomIdAndIdLessThanOrderByIdDesc(roomId, cursorId, pageRequest)
                    .map(ChatMessageResponse::new);
        }

        List<ChatMessageResponse> messages = slice.getContent();
        return new ChatMessageResponse.PageResult(messages, slice.hasNext());
    }

    private boolean isParticipant(Room room, Long userId) {
        return userId.equals(room.getUserId()) || userId.equals(room.getMasterId());
    }
}