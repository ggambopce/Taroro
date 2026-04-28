package com.neocompany.taroro.domain.message.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.message.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Slice<ChatMessage> findByRoomIdOrderByIdDesc(Long roomId, Pageable pageable);

    Slice<ChatMessage> findByRoomIdAndIdLessThanOrderByIdDesc(Long roomId, Long cursorId, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m WHERE m.id IN " +
           "(SELECT MAX(m2.id) FROM ChatMessage m2 WHERE m2.roomId IN :roomIds GROUP BY m2.roomId)")
    List<ChatMessage> findLatestByRoomIds(@Param("roomIds") List<Long> roomIds);

    long countByRoomIdAndIdGreaterThan(Long roomId, Long messageId);
}

