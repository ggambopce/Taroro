package com.neocompany.taroro.domain.masterplan.controller;

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

import com.neocompany.taroro.domain.masterplan.docs.MasterPlanControllerDocs;
import com.neocompany.taroro.domain.masterplan.dto.request.CreateMasterPlanRequest;
import com.neocompany.taroro.domain.masterplan.dto.request.UpdateMasterPlanRequest;
import com.neocompany.taroro.domain.masterplan.dto.response.MasterPlanResponse;
import com.neocompany.taroro.domain.masterplan.service.MasterPlanCommandService;
import com.neocompany.taroro.domain.masterplan.service.MasterPlanQueryService;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/master-plans")
@RequiredArgsConstructor
public class MasterPlanHttpController implements MasterPlanControllerDocs {

    private final MasterPlanQueryService queryService;
    private final MasterPlanCommandService commandService;

    @Override
    @GetMapping
    public GlobalApiResponse<PageResult<MasterPlanResponse>> getList(
            @RequestParam(required = false) Long masterId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return GlobalApiResponse.ok("플랜 목록 조회 성공",
                queryService.getPublicPlans(masterId, isActive, limit, offset));
    }

    @Override
    @GetMapping("/{planId}")
    public GlobalApiResponse<MasterPlanResponse> getPlan(@PathVariable Long planId) {
        return GlobalApiResponse.ok("플랜 상세 조회 성공", queryService.getPlan(planId));
    }

    @Override
    @PostMapping
    public GlobalApiResponse<?> create(
            @RequestBody CreateMasterPlanRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long planId = commandService.create(principal.getUser().getUserId(), request);
        return GlobalApiResponse.ok("플랜 등록 성공", Map.of("planId", planId));
    }

    @Override
    @PatchMapping("/{planId}")
    public GlobalApiResponse<?> update(
            @PathVariable Long planId,
            @RequestBody UpdateMasterPlanRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        commandService.update(principal.getUser().getUserId(), planId, request);
        return GlobalApiResponse.ok("플랜 수정 성공", Map.of("planId", planId));
    }

    @Override
    @DeleteMapping("/{planId}")
    public GlobalApiResponse<?> delete(
            @PathVariable Long planId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        commandService.delete(principal.getUser().getUserId(), planId);
        return GlobalApiResponse.ok("플랜 삭제 성공", null);
    }
}
