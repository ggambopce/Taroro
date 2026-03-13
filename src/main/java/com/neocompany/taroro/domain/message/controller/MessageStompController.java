package com.neocompany.taroro.domain.message.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.neocompany.taroro.domain.message.dto.request.ReadMessageRequest;
import com.neocompany.taroro.domain.message.dto.request.SendMessageRequest;
import com.neocompany.taroro.domain.message.dto.request.TypingRequest;
import com.neocompany.taroro.domain.message.service.MessageCommandService;
import com.neocompany.taroro.domain.message.service.MessageEventPublishService;
import com.neocompany.taroro.domain.message.service.ReadReceiptService;
import com.neocompany.taroro.domain.message.service.TypingEventService;
import com.neocompany.taroro.global.sessions.SessionPrincipal;

import lombok.RequiredArgsConstructor;

/**
 * STOMP 메시지 도메인 명령 처리
 *
 * 구독:  /topic/room.{roomId}
 * 명령:  /app/rooms/{roomId}/messages   — 메시지 전송
 *        /app/rooms/{roomId}/read       — 읽음 처리
 *        /app/rooms/{roomId}/typing     — 타이핑 상태
 */
@Controller
@RequiredArgsConstructor
public class MessageStompController {

    private final MessageCommandService messageCommandService;
    private final ReadReceiptService readReceiptService;
    private final TypingEventService typingEventService;
    private final MessageEventPublishService messageEventPublishService;

    @MessageMapping("/rooms/{roomId}/messages")
    public void sendMessage(@DestinationVariable Long roomId,
                            @Payload SendMessageRequest request,
                            Principal principal) {
        messageEventPublishService.publishMessage(roomId,
                messageCommandService.send(roomId, userId(principal), request));
    }

    @MessageMapping("/rooms/{roomId}/read")
    public void readMessage(@DestinationVariable Long roomId,
                            @Payload ReadMessageRequest request,
                            Principal principal) {
        messageEventPublishService.publishReadUpdated(roomId,
                readReceiptService.updateReadReceipt(roomId, userId(principal), request.getLastReadMessageId()));
    }

    @MessageMapping("/rooms/{roomId}/typing")
    public void typing(@DestinationVariable Long roomId,
                       @Payload TypingRequest request,
                       Principal principal) {
        messageEventPublishService.publishTyping(roomId,
                typingEventService.typing(roomId, userId(principal), request.isTyping()));
    }

    private Long userId(Principal principal) {
        return ((SessionPrincipal) principal).getUserId();
    }
}
