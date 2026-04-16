package com.neocompany.taroro.domain.tarocard.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.tarocard.docs.TaroCardControllerDocs;
import com.neocompany.taroro.domain.tarocard.dto.request.CreateTaroCardRequest;
import com.neocompany.taroro.domain.tarocard.dto.request.UpdateTaroCardRequest;
import com.neocompany.taroro.domain.tarocard.dto.response.TaroCardResponse;
import com.neocompany.taroro.domain.tarocard.dto.response.TaroCardSummaryResponse;
import com.neocompany.taroro.domain.tarocard.service.TaroCardCommandService;
import com.neocompany.taroro.domain.tarocard.service.TaroCardQueryService;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/taro-cards")
@RequiredArgsConstructor
public class TaroCardHttpController implements TaroCardControllerDocs {

    private final TaroCardQueryService queryService;
    private final TaroCardCommandService commandService;

    @Override
    @GetMapping("/sets/{setId}")
    public GlobalApiResponse<PageResult<TaroCardSummaryResponse>> getCardsBySet(
            @PathVariable Long setId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return GlobalApiResponse.ok("카드 목록 조회 성공",
                queryService.getCardsBySet(setId, keyword, isActive, limit, offset));
    }

    @Override
    @GetMapping("/{cardId}")
    public GlobalApiResponse<TaroCardResponse> getCard(@PathVariable Long cardId) {
        return GlobalApiResponse.ok("카드 상세 조회 성공", queryService.getCard(cardId));
    }

    @Override
    @PostMapping
    public GlobalApiResponse<?> create(
            @RequestBody CreateTaroCardRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long cardId = commandService.create(principal.getUser().getUserId(), request);
        return GlobalApiResponse.ok("카드 등록 성공", Map.of("cardId", cardId));
    }

    @Override
    @PatchMapping("/{cardId}")
    public GlobalApiResponse<?> update(
            @PathVariable Long cardId,
            @RequestBody UpdateTaroCardRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        commandService.update(principal.getUser().getUserId(), cardId, request);
        return GlobalApiResponse.ok("카드 수정 성공", Map.of("cardId", cardId));
    }

    @Override
    @DeleteMapping("/{cardId}")
    public GlobalApiResponse<?> delete(
            @PathVariable Long cardId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        commandService.delete(principal.getUser().getUserId(), cardId);
        return GlobalApiResponse.ok("카드 삭제 성공", null);
    }
}
