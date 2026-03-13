package com.neocompany.taroro.domain.master;

import org.springframework.stereotype.Service;

import com.neocompany.taroro.domain.master.dto.MasterStatusEvent;
import com.neocompany.taroro.global.websocket.StompDestination;
import com.neocompany.taroro.global.websocket.StompEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MasterStatusPublishService {

    private final StompEventPublisher publisher;

    /**
     * 마스터 상태 변경 브로드캐스트
     * → /topic/masters/status
     */
    public void publish(MasterStatusEvent event) {
        publisher.broadcast(StompDestination.MASTERS_STATUS, event);
    }
}
