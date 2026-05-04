package com.neocompany.taroro.domain.room.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.neocompany.taroro.domain.room.dto.event.CardEvent;
import com.neocompany.taroro.domain.room.dto.request.PickCardsRequest;
import com.neocompany.taroro.domain.room.dto.request.RevealCardRequest;
import com.neocompany.taroro.domain.room.dto.request.SelectCardSetRequest;
import com.neocompany.taroro.domain.room.dto.request.SpreadCardsRequest;
import com.neocompany.taroro.domain.room.service.CardCommandService;
import com.neocompany.taroro.domain.room.service.CardEventPublishService;
import com.neocompany.taroro.global.sessions.SessionPrincipal;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CardStompController {

    private final CardCommandService cardCommandService;
    private final CardEventPublishService cardEventPublishService;

    /** 마스터: 카드 세트 선택 */
    @MessageMapping("/rooms/{roomId}/cards/set")
    public void selectCardSet(
            @DestinationVariable Long roomId,
            @Payload SelectCardSetRequest request,
            Principal principal) {
        Long userId = toUserId(principal);
        CardEvent event = cardCommandService.selectCardSet(roomId, userId, request);
        cardEventPublishService.publish(roomId, event);
    }

    /** 마스터: 카드 펼치기 */
    @MessageMapping("/rooms/{roomId}/cards/spread")
    public void spread(
            @DestinationVariable Long roomId,
            @Payload SpreadCardsRequest request,
            Principal principal) {
        Long userId = toUserId(principal);
        CardEvent event = cardCommandService.spread(roomId, userId, request);
        cardEventPublishService.publish(roomId, event);
    }

    /** 유저: 카드 선택 */
    @MessageMapping("/rooms/{roomId}/cards/pick")
    public void pick(
            @DestinationVariable Long roomId,
            @Payload PickCardsRequest request,
            Principal principal) {
        Long userId = toUserId(principal);
        CardEvent event = cardCommandService.pick(roomId, userId, request);
        cardEventPublishService.publish(roomId, event);
    }

    /** 마스터: 카드 공개 */
    @MessageMapping("/rooms/{roomId}/cards/reveal")
    public void reveal(
            @DestinationVariable Long roomId,
            @Payload RevealCardRequest request,
            Principal principal) {
        Long userId = toUserId(principal);
        CardEvent event = cardCommandService.reveal(roomId, userId, request);
        cardEventPublishService.publish(roomId, event);
    }

    /** 마스터: 리딩 초기화 */
    @MessageMapping("/rooms/{roomId}/cards/reset")
    public void reset(
            @DestinationVariable Long roomId,
            Principal principal) {
        Long userId = toUserId(principal);
        CardEvent event = cardCommandService.reset(roomId, userId);
        cardEventPublishService.publish(roomId, event);
    }

    private Long toUserId(Principal principal) {
        return ((SessionPrincipal) principal).getUserId();
    }
}
