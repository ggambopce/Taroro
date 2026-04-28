package com.neocompany.taroro.domain.message.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neocompany.taroro.domain.message.entity.MessageRead;

public interface MessageReadRepository extends JpaRepository<MessageRead, Long> {

    Optional<MessageRead> findByRoomIdAndUserId(Long roomId, Long userId);

    List<MessageRead> findByUserIdAndRoomIdIn(Long userId, List<Long> roomIds);

    List<MessageRead> findByRoomId(Long roomId);
}
