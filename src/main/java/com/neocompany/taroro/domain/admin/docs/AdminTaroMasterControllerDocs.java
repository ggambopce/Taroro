package com.neocompany.taroro.domain.admin.docs;

import com.neocompany.taroro.domain.admin.dto.request.MasterApprovalRequest;
import com.neocompany.taroro.domain.taromaster.dto.response.TaroMasterResponse;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin - TaroMaster", description = "관리자 마스터 관리 API")
public interface AdminTaroMasterControllerDocs {

    @Operation(summary = "전체 마스터 목록 조회", description = "승인 상태 구분 없이 전체 마스터 목록을 조회합니다.")
    GlobalApiResponse<PageResult<TaroMasterResponse>> getList(
        String keyword,
        String status,
        int limit,
        int offset
    );

    @Operation(summary = "마스터 상세 조회", description = "관리자 권한으로 비공개/미승인 마스터도 조회 가능합니다.")
    GlobalApiResponse<TaroMasterResponse> get(Long masterId);

    @Operation(summary = "마스터 승인/반려", description = "마스터 신청을 승인(APPROVED) 또는 반려(REJECTED)합니다. 승인 시 User.isTaroMaster가 true로 변경됩니다.")
    GlobalApiResponse<?> approve(
        Long masterId,
        MasterApprovalRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
