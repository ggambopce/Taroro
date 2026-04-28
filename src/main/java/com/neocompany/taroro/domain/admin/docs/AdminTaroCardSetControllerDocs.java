package com.neocompany.taroro.domain.admin.docs;

import com.neocompany.taroro.domain.tarocardset.dto.request.CreateTaroCardSetRequest;
import com.neocompany.taroro.domain.tarocardset.dto.request.UpdateTaroCardSetRequest;
import com.neocompany.taroro.domain.tarocardset.dto.response.TaroCardSetResponse;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin - TaroCardSet", description = "관리자 타로 카드 세트 관리 API")
public interface AdminTaroCardSetControllerDocs {

    @Operation(summary = "카드 세트 목록 조회", description = "공개된 타로 카드 세트 목록을 조회합니다. masterName 필드가 포함됩니다.")
    GlobalApiResponse<PageResult<TaroCardSetResponse>> getList(
        String keyword, Long masterId, Boolean isActive, int limit, int offset
    );

    @Operation(summary = "카드 세트 상세 조회", description = "관리자 권한으로 비공개 세트도 조회 가능합니다.")
    GlobalApiResponse<TaroCardSetResponse> get(Long setId);

    @Operation(summary = "내 카드 세트 목록 조회", description = "로그인한 마스터 본인의 카드 세트 목록을 조회합니다.")
    GlobalApiResponse<PageResult<TaroCardSetResponse>> getMySets(
        int limit, int offset,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "내 카드 세트 등록")
    GlobalApiResponse<?> create(
        CreateTaroCardSetRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "내 카드 세트 수정")
    GlobalApiResponse<?> update(
        Long masterCardSetId,
        UpdateTaroCardSetRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "내 카드 세트 삭제")
    GlobalApiResponse<?> delete(
        Long masterCardSetId,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
