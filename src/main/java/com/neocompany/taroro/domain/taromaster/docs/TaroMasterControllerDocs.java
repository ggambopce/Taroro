package com.neocompany.taroro.domain.taromaster.docs;

import com.neocompany.taroro.domain.taromaster.dto.request.CreateTaroMasterRequest;
import com.neocompany.taroro.domain.taromaster.dto.request.UpdateTaroMasterRequest;
import com.neocompany.taroro.domain.taromaster.dto.response.TaroMasterResponse;
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

@Tag(name = "TaroMaster", description = "타로 마스터 관리 API")
public interface TaroMasterControllerDocs {

    @Operation(summary = "마스터 등록 신청", description = "일반 사용자가 타로 마스터 등록을 신청합니다. 승인 상태는 PENDING으로 저장됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "신청 완료",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "마스터 등록 신청 완료",
                      "statusCode": 200,
                      "data": { "masterId": 22, "approvalStatus": "PENDING" }
                    }
                    """)))
    })
    GlobalApiResponse<?> apply(
        CreateTaroMasterRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "마스터 목록 조회", description = "승인 완료된 공개 마스터 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "마스터 목록 조회 성공",
                      "statusCode": 200,
                      "data": {
                        "items": [
                          {
                            "masterId": 22,
                            "displayName": "별빛타로",
                            "status": "ONLINE",
                            "approvalStatus": "APPROVED",
                            "isPublic": true
                          }
                        ],
                        "limit": 20,
                        "offset": 0,
                        "hasNext": false
                      }
                    }
                    """)))
    })
    GlobalApiResponse<PageResult<TaroMasterResponse>> getList(
        @Parameter(description = "검색어 (마스터명/소개)") String keyword,
        @Parameter(description = "상태 필터 (ONLINE/BUSY/BREAK/OFFLINE)") String status,
        @Parameter(description = "조회 개수 (기본 20)") int limit,
        @Parameter(description = "조회 시작 위치") int offset
    );

    @Operation(summary = "내 마스터 정보 조회", description = "로그인한 사용자의 마스터 정보를 조회합니다.")
    GlobalApiResponse<TaroMasterResponse> getMe(
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "마스터 상세 조회", description = "특정 마스터의 공개 프로필을 조회합니다. 비공개/미승인 상태는 본인만 조회 가능합니다.")
    GlobalApiResponse<TaroMasterResponse> get(
        Long masterId,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "마스터 정보 수정", description = "로그인한 마스터가 본인 프로필을 수정합니다.")
    GlobalApiResponse<?> update(
        UpdateTaroMasterRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
