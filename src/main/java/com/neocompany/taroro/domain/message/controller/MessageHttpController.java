package com.neocompany.taroro.domain.message.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.message.dto.response.ChatMessageResponse;
import com.neocompany.taroro.domain.message.service.MessageService;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms/{roomId}/messages")
@RequiredArgsConstructor
public class MessageHttpController {

    private final MessageService messageService;

    /**
     * 메시지 목록 조회 (커서 기반 페이징)
     *
     * GET /api/rooms/{roomId}/messages?cursor=123&size=20
     * cursor 없으면 최신 20개, 있으면 해당 id 이전 메시지
     */
    @GetMapping
    public GlobalApiResponse<ChatMessageResponse.PageResult> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal PrincipalDetails principal) {

        return GlobalApiResponse.ok("메시지 목록 조회 성공",
                messageService.getMessages(roomId, principal.getUser().getUserId(), cursor, size));
    }
}
