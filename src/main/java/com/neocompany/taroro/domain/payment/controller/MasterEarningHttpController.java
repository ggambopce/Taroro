package com.neocompany.taroro.domain.payment.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.payment.dto.response.MasterEarningLedgerResponse;
import com.neocompany.taroro.domain.payment.dto.response.MasterEarningWalletResponse;
import com.neocompany.taroro.domain.payment.service.MasterEarningQueryService;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/master-auth/earnings")
@RequiredArgsConstructor
public class MasterEarningHttpController {

    private final MasterEarningQueryService queryService;

    @GetMapping("/me")
    public GlobalApiResponse<MasterEarningWalletResponse> getMyEarnings(
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getUserId();
        return GlobalApiResponse.ok("내 적립금 조회 성공", queryService.getMyEarnings(userId));
    }

    @GetMapping("/me/ledger")
    public GlobalApiResponse<PageResult<MasterEarningLedgerResponse>> getMyLedger(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getUserId();
        return GlobalApiResponse.ok("적립 내역 조회 성공",
                queryService.getMyLedger(userId, limit, offset));
    }
}
