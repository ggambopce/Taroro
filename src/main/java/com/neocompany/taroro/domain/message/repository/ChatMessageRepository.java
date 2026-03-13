package com.neocompany.taroro.domain.message.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.neocompany.taroro.domain.message.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /** 방의 메시지 목록 — 최신순, 커서 기반 페이징 */
    Slice<ChatMessage> findByRoomIdOrderByIdDesc(Long roomId, Pageable pageable);

    /** 특정 메시지 이전(오래된) 메시지 — 무한 스크롤 */
    Slice<ChatMessage> findByRoomIdAndIdLessThanOrderByIdDesc(Long roomId, Long cursorId, Pageable pageable);
}

