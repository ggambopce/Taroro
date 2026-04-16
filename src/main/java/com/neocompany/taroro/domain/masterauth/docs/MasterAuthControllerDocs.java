package com.neocompany.taroro.domain.masterauth.docs;

import com.neocompany.taroro.domain.masterauth.dto.request.CreateSettlementRequest;
import com.neocompany.taroro.domain.masterauth.dto.request.PassVerificationRequest;
import com.neocompany.taroro.domain.masterauth.dto.request.UpdateSettlementRequest;
import com.neocompany.taroro.domain.masterauth.dto.response.SettlementResponse;
import com.neocompany.taroro.domain.masterauth.dto.response.VerificationResponse;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MasterAuth", description = "마스터 인증/정산 관리 API")
public interface MasterAuthControllerDocs {

    @Operation(summary = "내 정산 정보 조회", description = "로그인한 마스터의 정산 계좌 정보를 조회합니다.")
    GlobalApiResponse<SettlementResponse> getMySettlement(
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "정산 정보 등록", description = "마스터의 정산 계좌 정보를 최초 등록합니다. 중복 등록 불가.")
    GlobalApiResponse<?> createSettlement(
        CreateSettlementRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "정산 정보 수정", description = "마스터의 정산 계좌 정보를 수정합니다. 계좌번호 변경 시 인증 상태가 초기화됩니다.")
    GlobalApiResponse<?> updateSettlement(
        UpdateSettlementRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "내 인증 정보 조회", description = "로그인한 마스터의 본인인증 상태를 조회합니다.")
    GlobalApiResponse<VerificationResponse> getMyVerification(
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "PASS 본인인증", description = "PASS 앱을 통한 본인인증을 처리합니다.")
    GlobalApiResponse<?> processPassVerification(
        PassVerificationRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
