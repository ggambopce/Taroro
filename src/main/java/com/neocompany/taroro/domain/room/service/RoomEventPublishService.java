package com.neocompany.taroro.domain.room.service;

import org.springframework.stereotype.Service;

import com.neocompany.taroro.domain.room.dto.event.RoomEvent;
import com.neocompany.taroro.global.websocket.StompDestination;
import com.neocompany.taroro.global.websocket.StompEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomEventPublishService {

    private final StompEventPublisher publisher;

    /** 방 이벤트 브로드캐스트 → /topic/room.{roomId} */
    public void publish(Long roomId, RoomEvent event) {
        publisher.broadcast(StompDestination.room(roomId), event);
    }
}
