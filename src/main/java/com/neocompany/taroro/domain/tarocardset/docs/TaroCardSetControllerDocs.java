package com.neocompany.taroro.domain.tarocardset.docs;

import com.neocompany.taroro.domain.tarocardset.dto.request.CreateTaroCardSetRequest;
import com.neocompany.taroro.domain.tarocardset.dto.request.UpdateTaroCardSetRequest;
import com.neocompany.taroro.domain.tarocardset.dto.response.TaroCardSetResponse;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "TaroCardSet", description = "타로 카드 세트 관리 API")
public interface TaroCardSetControllerDocs {

    @Operation(summary = "카드 세트 목록 조회", description = "공개된 타로 카드 세트 목록을 조회합니다.")
    GlobalApiResponse<PageResult<TaroCardSetResponse>> getList(
        String keyword, Long masterId, Boolean isActive, int limit, int offset
    );

    @Operation(summary = "카드 세트 상세 조회", description = "카드 세트 상세 정보를 조회합니다. 비공개 세트는 본인 마스터만 조회 가능합니다.")
    GlobalApiResponse<TaroCardSetResponse> get(
        Long setId,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "내 카드 세트 목록 조회", description = "로그인한 마스터 본인의 카드 세트 목록을 조회합니다.")
    GlobalApiResponse<PageResult<TaroCardSetResponse>> getMySets(
        int limit, int offset,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "내 카드 세트 등록", description = "로그인한 마스터가 자신의 카드 세트를 등록합니다.")
    GlobalApiResponse<?> create(
        CreateTaroCardSetRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "내 카드 세트 수정", description = "로그인한 마스터가 자신의 카드 세트를 수정합니다.")
    GlobalApiResponse<?> update(
        Long masterCardSetId,
        UpdateTaroCardSetRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "내 카드 세트 삭제", description = "로그인한 마스터가 자신의 카드 세트를 삭제합니다.")
    GlobalApiResponse<?> delete(
        Long masterCardSetId,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
