package com.neocompany.taroro.domain.room.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.neocompany.taroro.domain.room.service.RoomCommandService;
import com.neocompany.taroro.domain.room.service.RoomEventPublishService;
import com.neocompany.taroro.global.sessions.SessionPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * STOMP 방 도메인 명령 처리
 *
 * 구독:  /topic/room.{roomId}
 * 명령:  /app/rooms/{roomId}/enter|leave|start|end
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomStompController {

    private final RoomCommandService roomCommandService;
    private final RoomEventPublishService roomEventPublishService;

    @MessageMapping("/rooms/{roomId}/enter")
    public void enter(@DestinationVariable Long roomId, Principal principal) {
        roomEventPublishService.publish(roomId,
                roomCommandService.enter(roomId, userId(principal)));
    }

    @MessageMapping("/rooms/{roomId}/leave")
    public void leave(@DestinationVariable Long roomId, Principal principal) {
        roomEventPublishService.publish(roomId,
                roomCommandService.leave(roomId, userId(principal)));
    }

    @MessageMapping("/rooms/{roomId}/start")
    public void start(@DestinationVariable Long roomId, Principal principal) {
        roomEventPublishService.publish(roomId,
                roomCommandService.start(roomId, userId(principal)));
    }

    @MessageMapping("/rooms/{roomId}/end")
    public void end(@DestinationVariable Long roomId, Principal principal) {
        roomEventPublishService.publish(roomId,
                roomCommandService.end(roomId, userId(principal)));
    }

    private Long userId(Principal principal) {
        return ((SessionPrincipal) principal).getUserId();
    }
}

