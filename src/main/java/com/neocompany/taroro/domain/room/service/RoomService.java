package com.neocompany.taroro.domain.room.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.message.entity.ChatMessage;
import com.neocompany.taroro.domain.message.entity.MessageRead;
import com.neocompany.taroro.domain.message.repository.ChatMessageRepository;
import com.neocompany.taroro.domain.message.repository.MessageReadRepository;
import com.neocompany.taroro.domain.message.service.UnreadCountService;
import com.neocompany.taroro.domain.room.dto.RoomDetailResponse;
import com.neocompany.taroro.domain.room.dto.RoomSummaryResponse;
import com.neocompany.taroro.domain.room.dto.WaitingRoomResponse;
import com.neocompany.taroro.domain.room.dto.WaitingRoomResponse.WaitingRoomItem;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.entity.RoomParticipant;
import com.neocompany.taroro.domain.room.entity.RoomStatus;
import com.neocompany.taroro.domain.room.repository.RoomParticipantRepository;
import com.neocompany.taroro.domain.room.repository.RoomRepository;
import com.neocompany.taroro.domain.users.User;
import com.neocompany.taroro.domain.users.UserRepository;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageReadRepository messageReadRepository;
    private final UserRepository userRepository;
    private final OnlineStatusService onlineStatusService;
    private final UnreadCountService unreadCountService;

    public RoomDetailResponse getRoomDetail(Long roomId, Long requesterId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        if (!room.isParticipant(requesterId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }

        List<RoomParticipant> participants = participantRepository.findByRoomId(roomId);

        List<Long> userIds = participants.stream().map(RoomParticipant::getUserId).toList();
        Map<Long, String> userNameMap = userRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u.getName() != null ? u.getName() : ""));
        Map<Long, Boolean> onlineMap = onlineStatusService.isOnlineBatch(userIds);

        return RoomDetailResponse.of(room, participants, userNameMap, onlineMap);
    }

    public PageResult<RoomSummaryResponse> getMyRooms(Long userId, int limit, int offset) {
        Slice<Room> slice = roomRepository.findMyRooms(userId, PageRequest.of(offset / limit, limit));
        List<Room> rooms = slice.getContent();
        List<Long> roomIds = rooms.stream().map(Room::getId).toList();

        Map<Long, ChatMessage> lastMsgMap = chatMessageRepository.findLatestByRoomIds(roomIds)
                .stream().collect(Collectors.toMap(ChatMessage::getRoomId, m -> m));
        Map<Long, Long> lastReadMap = messageReadRepository.findByUserIdAndRoomIdIn(userId, roomIds)
                .stream().collect(Collectors.toMap(MessageRead::getRoomId, MessageRead::getLastReadMessageId));

        Map<Long, Long> cachedUnread = unreadCountService.getBatch(userId, roomIds);

        List<RoomSummaryResponse> items = rooms.stream().map(room -> {
            ChatMessage lastMsg = lastMsgMap.get(room.getId());
            long lastReadId = lastReadMap.getOrDefault(room.getId(), 0L);
            long cached = cachedUnread.getOrDefault(room.getId(), -1L);
            long unread = cached >= 0 ? cached
                    : chatMessageRepository.countByRoomIdAndIdGreaterThan(room.getId(), lastReadId);
            return RoomSummaryResponse.of(room, lastMsg, unread);
        }).toList();

        return PageResult.of(items, limit, offset);
    }

    public WaitingRoomResponse getWaitingRooms(int limit, int offset) {
        long waitingCount = roomRepository.countByStatus(RoomStatus.WAITING);

        Slice<Room> slice = roomRepository.findByStatusOrderByCreatedAtDesc(
                RoomStatus.WAITING, PageRequest.of(offset / limit, limit));

        AtomicInteger seq = new AtomicInteger(offset + 1);
        List<WaitingRoomItem> items = slice.getContent().stream()
                .map(r -> new WaitingRoomItem(
                        seq.getAndIncrement(),
                        r.getId(),
                        r.getMasterId(),
                        r.getMasterName(),
                        r.getRoomName(),
                        r.getCreatedAt()))
                .toList();

        return new WaitingRoomResponse(waitingCount, items);
    }
}
