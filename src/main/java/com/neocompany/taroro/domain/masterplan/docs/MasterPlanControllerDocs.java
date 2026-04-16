package com.neocompany.taroro.domain.masterplan.docs;

import com.neocompany.taroro.domain.masterplan.dto.request.CreateMasterPlanRequest;
import com.neocompany.taroro.domain.masterplan.dto.request.UpdateMasterPlanRequest;
import com.neocompany.taroro.domain.masterplan.dto.response.MasterPlanResponse;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MasterPlan", description = "마스터 상담 플랜 관리 API")
public interface MasterPlanControllerDocs {

    @Operation(summary = "플랜 목록 조회", description = "공개된 마스터 플랜 목록을 조회합니다.")
    GlobalApiResponse<PageResult<MasterPlanResponse>> getList(
        Long masterId,
        Boolean isActive,
        int limit,
        int offset
    );

    @Operation(summary = "플랜 상세 조회", description = "마스터 플랜 상세 정보를 조회합니다.")
    GlobalApiResponse<MasterPlanResponse> getPlan(Long planId);

    @Operation(summary = "플랜 등록", description = "마스터가 상담 플랜을 등록합니다.")
    GlobalApiResponse<?> create(
        CreateMasterPlanRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "플랜 수정", description = "마스터가 자신의 상담 플랜을 수정합니다.")
    GlobalApiResponse<?> update(
        Long planId,
        UpdateMasterPlanRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "플랜 삭제", description = "마스터가 자신의 상담 플랜을 삭제합니다.")
    GlobalApiResponse<?> delete(
        Long planId,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
