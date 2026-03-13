package com.neocompany.taroro.domain.message.service;

import org.springframework.stereotype.Service;

import com.neocompany.taroro.domain.message.dto.event.ChatMessageEvent;
import com.neocompany.taroro.domain.message.dto.event.ReadUpdatedEvent;
import com.neocompany.taroro.domain.message.dto.event.TypingEvent;
import com.neocompany.taroro.global.websocket.StompDestination;
import com.neocompany.taroro.global.websocket.StompEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageEventPublishService {

    private final StompEventPublisher publisher;

    /** 메시지 전송 이벤트 → /topic/room.{roomId} */
    public void publishMessage(Long roomId, ChatMessageEvent event) {
        publisher.broadcast(StompDestination.room(roomId), event);
    }

    /** 읽음 처리 이벤트 → /topic/room.{roomId} */
    public void publishReadUpdated(Long roomId, ReadUpdatedEvent event) {
        publisher.broadcast(StompDestination.room(roomId), event);
    }

    /** 타이핑 이벤트 → /topic/room.{roomId} */
    public void publishTyping(Long roomId, TypingEvent event) {
        publisher.broadcast(StompDestination.room(roomId), event);
    }
}
