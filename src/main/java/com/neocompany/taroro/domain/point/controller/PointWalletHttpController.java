package com.neocompany.taroro.domain.point.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.point.dto.response.PointLedgerResponse;
import com.neocompany.taroro.domain.point.dto.response.PointWalletResponse;
import com.neocompany.taroro.domain.point.service.PointWalletQueryService;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointWalletHttpController {

    private final PointWalletQueryService queryService;

    @GetMapping("/wallet/me")
    public GlobalApiResponse<PointWalletResponse> getMyWallet(
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getUserId();
        return GlobalApiResponse.ok("내 지갑 조회 성공", queryService.getMyWallet(userId));
    }

    @GetMapping("/ledger/me")
    public GlobalApiResponse<PageResult<PointLedgerResponse>> getMyLedger(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getUserId();
        return GlobalApiResponse.ok("포인트 내역 조회 성공",
                queryService.getMyLedger(userId, limit, offset));
    }
}
