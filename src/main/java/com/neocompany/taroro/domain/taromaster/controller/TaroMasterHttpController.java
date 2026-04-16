package com.neocompany.taroro.domain.taromaster.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.taromaster.docs.TaroMasterControllerDocs;
import com.neocompany.taroro.domain.taromaster.dto.request.CreateTaroMasterRequest;
import com.neocompany.taroro.domain.taromaster.dto.request.UpdateTaroMasterRequest;
import com.neocompany.taroro.domain.taromaster.dto.response.TaroMasterResponse;
import com.neocompany.taroro.domain.taromaster.service.TaroMasterCommandService;
import com.neocompany.taroro.domain.taromaster.service.TaroMasterQueryService;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/api/taro-masters")
@RequiredArgsConstructor
public class TaroMasterHttpController implements TaroMasterControllerDocs {

    private final TaroMasterQueryService queryService;
    private final TaroMasterCommandService commandService;

    @Override
    @PostMapping
    public GlobalApiResponse<?> apply(
            @RequestBody CreateTaroMasterRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long masterId = commandService.apply(principal.getUser().getUserId(), request);
        return GlobalApiResponse.ok("마스터 등록 신청 완료",
                Map.of("masterId", masterId, "approvalStatus", "PENDING"));
    }

    @Override
    @GetMapping
    public GlobalApiResponse<PageResult<TaroMasterResponse>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return GlobalApiResponse.ok("마스터 목록 조회 성공",
                queryService.getPublicMasters(keyword, status, limit, offset));
    }

    // /me 경로가 /{masterId} 보다 먼저 매핑되도록 메서드 순서 유지
    @Override
    @GetMapping("/me")
    public GlobalApiResponse<TaroMasterResponse> getMe(
            @AuthenticationPrincipal PrincipalDetails principal) {
        return GlobalApiResponse.ok("내 마스터 정보 조회 성공",
                queryService.getMyMaster(principal.getUser().getUserId()));
    }

    @Override
    @GetMapping("/{masterId}")
    public GlobalApiResponse<TaroMasterResponse> get(
            @PathVariable Long masterId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long requesterId = (principal != null) ? principal.getUser().getUserId() : null;
        return GlobalApiResponse.ok("마스터 조회 성공",
                queryService.getMaster(masterId, requesterId));
    }

    @Override
    @PatchMapping("/me")
    public GlobalApiResponse<?> update(
            @RequestBody UpdateTaroMasterRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long masterId = commandService.update(principal.getUser().getUserId(), request);
        return GlobalApiResponse.ok("마스터 정보 수정 성공", Map.of("masterId", masterId));
    }
}
