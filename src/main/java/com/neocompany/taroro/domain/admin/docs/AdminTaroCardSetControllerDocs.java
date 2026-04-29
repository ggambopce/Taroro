package com.neocompany.taroro.domain.admin.docs;

import com.neocompany.taroro.domain.tarocardset.dto.request.CreateTaroCardSetRequest;
import com.neocompany.taroro.domain.tarocardset.dto.request.UpdateTaroCardSetRequest;
import com.neocompany.taroro.domain.tarocardset.dto.response.TaroCardSetResponse;
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

@Tag(name = "Admin - TaroCardSet", description = "관리자 타로 카드 세트 관리 API")
public interface AdminTaroCardSetControllerDocs {

    @Operation(
        summary = "카드 세트 목록 조회",
        description = """
            공개된 타로 카드 세트 목록을 조회합니다.
            - 응답에 `masterName` 필드가 포함됩니다 (마스터 displayName).
            - keyword: setName 또는 setDescription 포함 검색
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "카드 세트 목록",
                      "statusCode": 200,
                      "data": {
                        "items": [
                          {
                            "setId": 1,
                            "masterId": 1,
                            "masterName": "타로마스터홍길동",
                            "setName": "라이더-웨이트 타로",
                            "setDescription": "클래식 타로 덱입니다.",
                            "brandName": "US Games",
                            "publisherName": "US Games Systems",
                            "coverImageUrl": "https://...",
                            "cardCount": 78,
                            "isActive": true,
                            "isPublic": true,
                            "createdAt": "2026-04-01T00:00:00Z",
                            "updatedAt": "2026-04-28T00:00:00Z"
                          }
                        ],
                        "limit": 20,
                        "offset": 0,
                        "hasNext": false
                      }
                    }
                    """)))
    })
    GlobalApiResponse<PageResult<TaroCardSetResponse>> getList(
        @Parameter(description = "검색 키워드") String keyword,
        @Parameter(description = "마스터 ID 필터") Long masterId,
        @Parameter(description = "활성 상태 필터") Boolean isActive,
        @Parameter(description = "조회 개수 (기본 20)") int limit,
        @Parameter(description = "조회 시작 위치 (기본 0)") int offset
    );

    @Operation(
        summary = "카드 세트 상세 조회",
        description = "관리자 권한으로 비공개 세트도 조회 가능합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "카드 세트 상세",
                      "statusCode": 200,
                      "data": {
                        "setId": 1,
                        "masterId": 1,
                        "setName": "라이더-웨이트 타로",
                        "setDescription": "클래식 타로 덱입니다.",
                        "brandName": "US Games",
                        "publisherName": "US Games Systems",
                        "coverImageUrl": "https://...",
                        "cardCount": 78,
                        "isActive": true,
                        "isPublic": false,
                        "createdAt": "2026-04-01T00:00:00Z",
                        "updatedAt": "2026-04-28T00:00:00Z"
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "200", description = "세트 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": false, "message": "카드 세트를 찾을 수 없습니다.", "statusCode": 201}
                    """)))
    })
    GlobalApiResponse<TaroCardSetResponse> get(
        @Parameter(description = "카드 세트 ID") Long setId
    );

    @Operation(
        summary = "내 카드 세트 목록 조회",
        description = "로그인한 마스터 본인의 카드 세트 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "내 카드 세트 목록",
                      "statusCode": 200,
                      "data": {
                        "items": [],
                        "limit": 20,
                        "offset": 0,
                        "hasNext": false
                      }
                    }
                    """)))
    })
    GlobalApiResponse<PageResult<TaroCardSetResponse>> getMySets(
        @Parameter(description = "조회 개수 (기본 20)") int limit,
        @Parameter(description = "조회 시작 위치 (기본 0)") int offset,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(
        summary = "카드 세트 등록",
        description = "로그인한 마스터의 카드 세트를 등록합니다. ROLE_ADMIN 필요."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "등록 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": true, "message": "카드 세트 등록 성공", "statusCode": 200}
                    """)))
    })
    GlobalApiResponse<?> create(
        CreateTaroCardSetRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "카드 세트 수정", description = "본인 소유 카드 세트를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": true, "message": "카드 세트 수정 성공", "statusCode": 200}
                    """)))
    })
    GlobalApiResponse<?> update(
        @Parameter(description = "카드 세트 ID") Long masterCardSetId,
        UpdateTaroCardSetRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "카드 세트 삭제", description = "본인 소유 카드 세트를 삭제(소프트 삭제)합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": true, "message": "카드 세트 삭제 성공", "statusCode": 200}
                    """)))
    })
    GlobalApiResponse<?> delete(
        @Parameter(description = "카드 세트 ID") Long masterCardSetId,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
