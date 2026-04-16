package com.neocompany.taroro.domain.masterauth.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.masterauth.docs.MasterAuthControllerDocs;
import com.neocompany.taroro.domain.masterauth.dto.request.CreateSettlementRequest;
import com.neocompany.taroro.domain.masterauth.dto.request.PassVerificationRequest;
import com.neocompany.taroro.domain.masterauth.dto.request.UpdateSettlementRequest;
import com.neocompany.taroro.domain.masterauth.dto.response.SettlementResponse;
import com.neocompany.taroro.domain.masterauth.dto.response.VerificationResponse;
import com.neocompany.taroro.domain.masterauth.service.MasterSettlementService;
import com.neocompany.taroro.domain.masterauth.service.MasterVerificationService;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/master-auth")
@RequiredArgsConstructor
public class MasterAuthHttpController implements MasterAuthControllerDocs {

    private final MasterSettlementService settlementService;
    private final MasterVerificationService verificationService;

    @Override
    @GetMapping("/settlement/me")
    public GlobalApiResponse<SettlementResponse> getMySettlement(
            @AuthenticationPrincipal PrincipalDetails principal) {
        return GlobalApiResponse.ok("정산 정보 조회 성공",
                settlementService.getMy(principal.getUser().getUserId()));
    }

    @Override
    @PostMapping("/settlement")
    public GlobalApiResponse<?> createSettlement(
            @RequestBody CreateSettlementRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long settlementId = settlementService.create(principal.getUser().getUserId(), request);
        return GlobalApiResponse.ok("정산 정보 등록 성공", Map.of("settlementId", settlementId));
    }

    @Override
    @PatchMapping("/settlement")
    public GlobalApiResponse<?> updateSettlement(
            @RequestBody UpdateSettlementRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        settlementService.update(principal.getUser().getUserId(), request);
        return GlobalApiResponse.ok("정산 정보 수정 성공", null);
    }

    @Override
    @GetMapping("/verification/me")
    public GlobalApiResponse<VerificationResponse> getMyVerification(
            @AuthenticationPrincipal PrincipalDetails principal) {
        return GlobalApiResponse.ok("인증 정보 조회 성공",
                verificationService.getMy(principal.getUser().getUserId()));
    }

    @Override
    @PostMapping("/verification/pass")
    public GlobalApiResponse<?> processPassVerification(
            @RequestBody PassVerificationRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        verificationService.processPassVerification(principal.getUser().getUserId(), request);
        return GlobalApiResponse.ok("PASS 본인인증 완료", null);
    }
}
