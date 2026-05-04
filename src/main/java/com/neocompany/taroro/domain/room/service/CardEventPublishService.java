package com.neocompany.taroro.domain.room.service;

import org.springframework.stereotype.Service;

import com.neocompany.taroro.domain.room.dto.event.CardEvent;
import com.neocompany.taroro.global.websocket.StompDestination;
import com.neocompany.taroro.global.websocket.StompEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardEventPublishService {

    private final StompEventPublisher publisher;

    public void publish(Long roomId, CardEvent event) {
        publisher.broadcast(StompDestination.room(roomId), event);
    }
}
