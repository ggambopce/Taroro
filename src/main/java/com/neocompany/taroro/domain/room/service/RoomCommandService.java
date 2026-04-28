package com.neocompany.taroro.domain.room.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.room.dto.CreateRoomRequest;
import com.neocompany.taroro.domain.room.dto.UpdateRoomRequest;
import com.neocompany.taroro.domain.room.dto.event.RoomEvent;
import com.neocompany.taroro.domain.room.dto.event.RoomEventType;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.entity.RoomParticipant;
import com.neocompany.taroro.domain.room.entity.RoomStatus;
import com.neocompany.taroro.domain.room.repository.RoomParticipantRepository;
import com.neocompany.taroro.domain.room.repository.RoomRepository;
import com.neocompany.taroro.domain.taromaster.entity.ApprovalStatus;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomCommandService {

    private final RoomRepository roomRepository;
    private final RoomParticipantRepository participantRepository;
    private final TaroMasterRepository masterRepository;

    public Room create(Long userId, CreateRoomRequest request) {
        TaroMaster master = masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
        if (master.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BusinessException(ErrorCode.MASTER_NOT_APPROVED, "승인된 마스터만 방을 생성할 수 있습니다.");
        }
        Room room = Room.createByMaster(userId, request.getRoomName(), master.getDisplayName());
        return roomRepository.save(room);
    }

    public void update(Long roomId, Long userId, UpdateRoomRequest request) {
        Room room = getRoom(roomId);
        if (!room.isMaster(userId)) throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        room.updateName(request.getRoomName());
    }

    public void closeRoom(Long roomId, Long userId) {
        Room room = getRoom(roomId);
        if (!room.isMaster(userId)) throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        if (room.getStatus() == RoomStatus.CLOSED) {
            throw new BusinessException(ErrorCode.ROOM_INVALID_STATUS, "이미 종료된 방입니다.");
        }
        room.close();
    }

    /**
     * 입장 — 참여자 기록 생성, 방 상태가 WAITING 이면 ACTIVE 로 전환
     */
    public RoomEvent enter(Long roomId, Long userId) {
        Room room = getRoom(roomId);
        requireParticipant(room, userId);

        // 이미 입장 중이면 중복 처리 방지
        participantRepository.findByRoomIdAndUserIdAndLeftAtIsNull(roomId, userId)
                .ifPresent(p -> { throw new BusinessException(ErrorCode.ROOM_INVALID_STATUS, "이미 입장 중입니다."); });

        participantRepository.save(RoomParticipant.join(roomId, userId));

        log.info("[Room] ENTER roomId={}, userId={}", roomId, userId);
        return RoomEvent.of(RoomEventType.ROOM_ENTER, roomId, userId, room.getStatus());
    }

    /**
     * 퇴장 — 참여 기록의 leftAt 업데이트
     */
    public RoomEvent leave(Long roomId, Long userId) {
        Room room = getRoom(roomId);
        requireParticipant(room, userId);

        participantRepository.findByRoomIdAndUserIdAndLeftAtIsNull(roomId, userId)
                .ifPresent(RoomParticipant::leave);

        log.info("[Room] LEAVE roomId={}, userId={}", roomId, userId);
        return RoomEvent.of(RoomEventType.ROOM_LEAVE, roomId, userId, room.getStatus());
    }

    /**
     * 상담 시작 — WAITING → ACTIVE (마스터 전용)
     */
    public RoomEvent start(Long roomId, Long userId) {
        Room room = getRoom(roomId);
        requireMaster(room, userId);

        room.start(); // 내부에서 상태 검증

        log.info("[Room] START roomId={}, masterId={}", roomId, userId);
        return RoomEvent.of(RoomEventType.ROOM_START, roomId, userId, room.getStatus());
    }

    /**
     * 상담 종료 — ACTIVE → CLOSED (마스터 전용)
     */
    public RoomEvent end(Long roomId, Long userId) {
        Room room = getRoom(roomId);
        requireMaster(room, userId);

        room.end(); // 내부에서 상태 검증

        log.info("[Room] END roomId={}, masterId={}", roomId, userId);
        return RoomEvent.of(RoomEventType.ROOM_END, roomId, userId, room.getStatus());
    }

    // ── 공통 ──────────────────────────────────────────────────────────────────

    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
    }

    private void requireParticipant(Room room, Long userId) {
        if (!room.isParticipant(userId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }
    }

    private void requireMaster(Room room, Long userId) {
        if (!room.isMaster(userId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }
    }
}
