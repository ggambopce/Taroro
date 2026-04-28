package com.neocompany.taroro.domain.admin.docs;

import com.neocompany.taroro.domain.tarocard.dto.request.CreateTaroCardRequest;
import com.neocompany.taroro.domain.tarocard.dto.request.UpdateTaroCardRequest;
import com.neocompany.taroro.domain.tarocard.dto.response.TaroCardResponse;
import com.neocompany.taroro.domain.tarocard.dto.response.TaroCardSummaryResponse;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin - TaroCard", description = "관리자 타로 카드 관리 API")
public interface AdminTaroCardControllerDocs {

    @Operation(summary = "세트별 카드 목록 조회")
    GlobalApiResponse<PageResult<TaroCardSummaryResponse>> getCardsBySet(
        Long setId, String keyword, Boolean isActive, int limit, int offset
    );

    @Operation(summary = "카드 상세 조회")
    GlobalApiResponse<TaroCardResponse> getCard(Long cardId);

    @Operation(summary = "카드 등록")
    GlobalApiResponse<?> create(
        CreateTaroCardRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "카드 수정")
    GlobalApiResponse<?> update(
        Long cardId,
        UpdateTaroCardRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "카드 삭제")
    GlobalApiResponse<?> delete(
        Long cardId,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
