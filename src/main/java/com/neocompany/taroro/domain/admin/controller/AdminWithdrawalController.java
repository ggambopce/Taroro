package com.neocompany.taroro.domain.admin.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.withdrawal.dto.request.RejectWithdrawalRequest;
import com.neocompany.taroro.domain.withdrawal.dto.response.WithdrawalResponse;
import com.neocompany.taroro.domain.withdrawal.enums.WithdrawalStatus;
import com.neocompany.taroro.domain.withdrawal.service.WithdrawalCommandService;
import com.neocompany.taroro.domain.withdrawal.service.WithdrawalQueryService;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/withdrawals")
@RequiredArgsConstructor
public class AdminWithdrawalController {

    private final WithdrawalCommandService commandService;
    private final WithdrawalQueryService queryService;

    @GetMapping
    public GlobalApiResponse<PageResult<WithdrawalResponse>> getAll(
            @RequestParam(required = false) WithdrawalStatus status,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return GlobalApiResponse.ok("출금 신청 목록 조회 성공",
                queryService.getAll(status, limit, offset));
    }

    @GetMapping("/{withdrawalId}")
    public GlobalApiResponse<WithdrawalResponse> getOne(@PathVariable Long withdrawalId) {
        return GlobalApiResponse.ok("출금 신청 상세 조회 성공", queryService.getOne(withdrawalId));
    }

    @PatchMapping("/{withdrawalId}/approve")
    public GlobalApiResponse<?> approve(
            @PathVariable Long withdrawalId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        commandService.approve(withdrawalId, principal.getUser().getUserId());
        return GlobalApiResponse.ok("출금 신청 승인", null);
    }

    @PatchMapping("/{withdrawalId}/reject")
    public GlobalApiResponse<?> reject(
            @PathVariable Long withdrawalId,
            @RequestBody @Valid RejectWithdrawalRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        commandService.reject(withdrawalId, principal.getUser().getUserId(), request.getReason());
        return GlobalApiResponse.ok("출금 신청 거절", null);
    }

    @PatchMapping("/{withdrawalId}/complete")
    public GlobalApiResponse<?> complete(
            @PathVariable Long withdrawalId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        commandService.complete(withdrawalId, principal.getUser().getUserId());
        return GlobalApiResponse.ok("출금 완료 표시", null);
    }
}
