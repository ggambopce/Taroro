package com.neocompany.taroro.domain.signaling;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.neocompany.taroro.domain.signaling.dto.SignalingEvent;
import com.neocompany.taroro.domain.signaling.dto.request.SignalingAnswerRequest;
import com.neocompany.taroro.domain.signaling.dto.request.SignalingIceRequest;
import com.neocompany.taroro.domain.signaling.dto.request.SignalingOfferRequest;
import com.neocompany.taroro.global.sessions.SessionPrincipal;

import lombok.RequiredArgsConstructor;

/**
 * WebRTC 시그널링 STOMP 컨트롤러
 *
 * SEND /app/rooms/{roomId}/signal/offer  — Offer SDP 전달
 * SEND /app/rooms/{roomId}/signal/answer — Answer SDP 전달
 * SEND /app/rooms/{roomId}/signal/ice    — ICE Candidate 전달
 *
 * 수신 채널: /user/{targetUserId}/queue/signaling
 */
@Controller
@RequiredArgsConstructor
public class SignalingStompController {

    private final SignalingEventPublishService signalingEventPublishService;

    @MessageMapping("/rooms/{roomId}/signal/offer")
    public void offer(@DestinationVariable Long roomId,
                      @Payload SignalingOfferRequest request,
                      Principal principal) {
        Long senderId = userId(principal);
        SignalingEvent event = SignalingEvent.of("OFFER", roomId, senderId, request.getSdp());
        signalingEventPublishService.publish(request.getTargetUserId(), event);
    }

    @MessageMapping("/rooms/{roomId}/signal/answer")
    public void answer(@DestinationVariable Long roomId,
                       @Payload SignalingAnswerRequest request,
                       Principal principal) {
        Long senderId = userId(principal);
        SignalingEvent event = SignalingEvent.of("ANSWER", roomId, senderId, request.getSdp());
        signalingEventPublishService.publish(request.getTargetUserId(), event);
    }

    @MessageMapping("/rooms/{roomId}/signal/ice")
    public void ice(@DestinationVariable Long roomId,
                    @Payload SignalingIceRequest request,
                    Principal principal) {
        Long senderId = userId(principal);
        SignalingEvent event = SignalingEvent.of("ICE_CANDIDATE", roomId, senderId, request.getCandidate());
        signalingEventPublishService.publish(request.getTargetUserId(), event);
    }

    private Long userId(Principal principal) {
        return ((SessionPrincipal) principal).getUserId();
    }
}
