package com.neocompany.taroro.domain.master;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.neocompany.taroro.domain.master.dto.MasterStatusRequest;
import com.neocompany.taroro.global.sessions.SessionPrincipal;

import lombok.RequiredArgsConstructor;

/**
 * 마스터 상태 STOMP 컨트롤러
 *
 * SEND /app/masters/status — 마스터 가용 상태 변경 브로드캐스트
 * 수신 채널: /topic/masters/status
 */
@Controller
@RequiredArgsConstructor
public class MasterStatusStompController {

    private final MasterStatusService masterStatusService;
    private final MasterStatusPublishService masterStatusPublishService;

    @MessageMapping("/masters/status")
    public void updateStatus(@Payload MasterStatusRequest request, Principal principal) {
        Long userId = ((SessionPrincipal) principal).getUserId();
        masterStatusPublishService.publish(masterStatusService.buildEvent(userId, request));
    }
}
