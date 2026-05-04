package com.neocompany.taroro.domain.message.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.message.dto.response.ChatMessageResponse;
import com.neocompany.taroro.domain.message.entity.ChatMessage;
import com.neocompany.taroro.domain.message.entity.MessageRead;
import com.neocompany.taroro.domain.message.repository.ChatMessageRepository;
import com.neocompany.taroro.domain.message.repository.MessageReadRepository;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.repository.RoomRepository;
import com.neocompany.taroro.domain.users.User;
import com.neocompany.taroro.domain.users.UserRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ChatMessageRepository messageRepository;
    private final MessageReadRepository messageReadRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageCacheService messageCacheService;

    public ChatMessageResponse.PageResult getMessages(Long roomId, Long requesterId, Long cursorId, int size) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        if (!isParticipant(room, requesterId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }

        int pageSize = (size > 0 && size <= 100) ? size : DEFAULT_PAGE_SIZE;

        // 첫 페이지(cursor 없음)는 캐시 우선 조회
        if (cursorId == null) {
            List<ChatMessageResponse> cached = messageCacheService.getRecent(roomId, pageSize);
            if (cached != null) {
                List<ChatMessageResponse> filtered = cached.stream().filter(m -> m != null).toList();
                return new ChatMessageResponse.PageResult(roomId, filtered, filtered.size() == pageSize);
            }
        }

        PageRequest pageRequest = PageRequest.of(0, pageSize);

        Slice<ChatMessage> slice;
        if (cursorId == null) {
            slice = messageRepository.findByRoomIdOrderByIdDesc(roomId, pageRequest);
        } else {
            slice = messageRepository.findByRoomIdAndIdLessThanOrderByIdDesc(roomId, cursorId, pageRequest);
        }

        List<ChatMessage> messages = slice.getContent();

        // 배치: 발신자 이름 조회
        Set<Long> senderIds = messages.stream().map(ChatMessage::getSenderId).collect(Collectors.toSet());
        Map<Long, String> nameMap = userRepository.findAllByUserIdIn(senderIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u.getName() != null ? u.getName() : ""));

        // 배치: 읽음 기록 조회 (readCount 계산용)
        List<MessageRead> reads = messageReadRepository.findByRoomId(roomId);

        List<ChatMessageResponse> responseList = messages.stream().map(msg -> {
            String senderName = nameMap.getOrDefault(msg.getSenderId(), "");
            String senderRole = msg.getSenderId().equals(room.getMasterId()) ? "MASTER" : "USER";
            long readCount = reads.stream()
                    .filter(r -> r.getLastReadMessageId() >= msg.getId())
                    .count();
            return new ChatMessageResponse(msg, senderName, senderRole, readCount);
        }).toList();

        return new ChatMessageResponse.PageResult(roomId, responseList, slice.hasNext());
    }

    private boolean isParticipant(Room room, Long userId) {
        return userId.equals(room.getUserId()) || userId.equals(room.getMasterId());
    }
}
