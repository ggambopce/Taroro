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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin - TaroCard", description = "관리자 타로 카드 관리 API")
public interface AdminTaroCardControllerDocs {

    @Operation(
        summary = "세트별 카드 목록 조회",
        description = "특정 카드 세트에 속한 카드 목록을 cardNumber 오름차순으로 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "카드 목록",
                      "statusCode": 200,
                      "result": {
                        "items": [
                          {
                            "cardId": 1,
                            "cardName": "바보",
                            "cardNumber": 0,
                            "arcanaType": "MAJOR",
                            "suit": null,
                            "imageUrl": "https://...",
                            "isActive": true
                          }
                        ],
                        "limit": 20,
                        "offset": 0,
                        "hasNext": false
                      }
                    }
                    """)))
    })
    GlobalApiResponse<PageResult<TaroCardSummaryResponse>> getCardsBySet(
        @Parameter(description = "카드 세트 ID") Long setId,
        @Parameter(description = "검색 키워드 (cardName)") String keyword,
        @Parameter(description = "활성 상태 필터") Boolean isActive,
        @Parameter(description = "조회 개수 (기본 20)") int limit,
        @Parameter(description = "조회 시작 위치 (기본 0)") int offset
    );

    @Operation(
        summary = "카드 상세 조회",
        description = "카드 ID로 카드 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "카드 상세",
                      "statusCode": 200,
                      "result": {
                        "cardId": 1,
                        "setId": 1,
                        "masterId": 1,
                        "cardName": "바보",
                        "cardNumber": 0,
                        "arcanaType": "MAJOR",
                        "suit": null,
                        "keywords": ["새로운 시작", "자유", "모험"],
                        "cardDescription": "바보 카드는 새로운 여정의 시작을 상징합니다.",
                        "uprightMeaning": "새로운 시작, 순수함, 자유",
                        "reversedMeaning": "무모함, 부주의, 방황",
                        "imageUrl": "https://...",
                        "isActive": true,
                        "createdAt": "2026-04-01T00:00:00Z",
                        "updatedAt": "2026-04-28T00:00:00Z"
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "200", description = "카드 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": false, "message": "카드를 찾을 수 없습니다.", "statusCode": 201}
                    """)))
    })
    GlobalApiResponse<TaroCardResponse> getCard(
        @Parameter(description = "카드 ID") Long cardId
    );

    @Operation(
        summary = "카드 등록",
        description = """
            타로 카드를 등록합니다.
            - `arcanaType`: MAJOR / MINOR
            - `suit`: WANDS / CUPS / SWORDS / PENTACLES (MINOR 아르카나만 해당, MAJOR는 null)
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "등록 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": true, "message": "카드 등록 성공", "statusCode": 200}
                    """)))
    })
    GlobalApiResponse<?> create(
        CreateTaroCardRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "카드 수정", description = "카드 정보를 수정합니다. 본인 소유 카드 세트의 카드만 수정 가능합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": true, "message": "카드 수정 성공", "statusCode": 200}
                    """)))
    })
    GlobalApiResponse<?> update(
        @Parameter(description = "카드 ID") Long cardId,
        UpdateTaroCardRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "카드 삭제", description = "카드를 삭제(소프트 삭제)합니다. 본인 소유 카드 세트의 카드만 삭제 가능합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": true, "message": "카드 삭제 성공", "statusCode": 200}
                    """)))
    })
    GlobalApiResponse<?> delete(
        @Parameter(description = "카드 ID") Long cardId,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
