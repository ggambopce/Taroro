package com.neocompany.taroro.domain.tarocard.docs;

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

@Tag(name = "TaroCard", description = "타로 카드 관리 API")
public interface TaroCardControllerDocs {

    @Operation(summary = "세트별 카드 목록 조회", description = "특정 카드 세트에 속한 카드 목록을 조회합니다.")
    GlobalApiResponse<PageResult<TaroCardSummaryResponse>> getCardsBySet(
        Long setId,
        String keyword,
        Boolean isActive,
        int limit,
        int offset
    );

    @Operation(summary = "카드 상세 조회", description = "타로 카드 상세 정보를 조회합니다.")
    GlobalApiResponse<TaroCardResponse> getCard(Long cardId);

    @Operation(summary = "카드 등록", description = "마스터가 자신의 카드 세트에 카드를 등록합니다.")
    GlobalApiResponse<?> create(
        CreateTaroCardRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "카드 수정", description = "마스터가 자신의 카드를 수정합니다.")
    GlobalApiResponse<?> update(
        Long cardId,
        UpdateTaroCardRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "카드 삭제", description = "마스터가 자신의 카드를 삭제합니다.")
    GlobalApiResponse<?> delete(
        Long cardId,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
