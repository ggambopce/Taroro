package com.neocompany.taroro.domain.room.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neocompany.taroro.domain.room.entity.RoomParticipant;

public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    Optional<RoomParticipant> findByRoomIdAndUserIdAndLeftAtIsNull(Long roomId, Long userId);

    List<RoomParticipant> findByRoomId(Long roomId);
}
