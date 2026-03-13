package com.neocompany.taroro.domain.master.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 마스터 상태 브로드캐스트 이벤트
 * → /topic/masters/status
 */
@Getter
@AllArgsConstructor
public class MasterStatusEvent {
    private final Long masterId;
    private final String nickname;
    private final String status;
    private final Instant timestamp;

    public static MasterStatusEvent of(Long masterId, String nickname, String status) {
        return new MasterStatusEvent(masterId, nickname, status, Instant.now());
    }
}
