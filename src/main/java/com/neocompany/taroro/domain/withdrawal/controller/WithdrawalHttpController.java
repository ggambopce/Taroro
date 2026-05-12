package com.neocompany.taroro.domain.withdrawal.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.withdrawal.dto.request.CreateWithdrawalRequest;
import com.neocompany.taroro.domain.withdrawal.dto.response.WithdrawalResponse;
import com.neocompany.taroro.domain.withdrawal.service.WithdrawalCommandService;
import com.neocompany.taroro.domain.withdrawal.service.WithdrawalQueryService;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/master-auth/withdrawals")
@RequiredArgsConstructor
public class WithdrawalHttpController {

    private final WithdrawalCommandService commandService;
    private final WithdrawalQueryService queryService;

    @PostMapping
    public GlobalApiResponse<?> requestWithdrawal(
            @RequestBody @Valid CreateWithdrawalRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getUserId();
        Long id = commandService.requestWithdrawal(userId, request.getAmount());
        return GlobalApiResponse.ok("출금 신청 완료", Map.of("withdrawalId", id));
    }

    @GetMapping
    public GlobalApiResponse<PageResult<WithdrawalResponse>> getMyWithdrawals(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getUserId();
        return GlobalApiResponse.ok("내 출금 신청 목록 조회 성공",
                queryService.getMyWithdrawals(userId, limit, offset));
    }

    @GetMapping("/{withdrawalId}")
    public GlobalApiResponse<WithdrawalResponse> getMyWithdrawal(
            @PathVariable Long withdrawalId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getUserId();
        return GlobalApiResponse.ok("출금 신청 상세 조회 성공",
                queryService.getMyWithdrawal(userId, withdrawalId));
    }
}
