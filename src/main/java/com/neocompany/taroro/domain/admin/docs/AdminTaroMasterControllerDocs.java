package com.neocompany.taroro.domain.admin.docs;

import com.neocompany.taroro.domain.admin.dto.request.MasterApprovalRequest;
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

@Tag(name = "Admin - TaroMaster", description = "관리자 마스터 관리 API")
public interface AdminTaroMasterControllerDocs {

    @Operation(
        summary = "전체 마스터 목록 조회",
        description = """
            승인 상태(PENDING/APPROVED/REJECTED) 구분 없이 전체 마스터 목록을 조회합니다.
            - keyword: displayName 또는 intro 포함 검색
            - status: ONLINE / BUSY / BREAK / OFFLINE 필터
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "마스터 목록",
                      "statusCode": 200,
                      "data": {
                        "items": [
                          {
                            "masterId": 1,
                            "userId": 10,
                            "displayName": "타로마스터홍길동",
                            "intro": "10년 경력의 타로 마스터입니다.",
                            "profileImageUrl": "https://...",
                            "specialties": ["LOVE", "CAREER"],
                            "careerYears": 10,
                            "status": "ONLINE",
                            "approvalStatus": "PENDING",
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
    GlobalApiResponse<PageResult<TaroMasterResponse>> getList(
        @Parameter(description = "검색 키워드 (displayName, intro)") String keyword,
        @Parameter(description = "온라인 상태 필터 (ONLINE/BUSY/BREAK/OFFLINE)") String status,
        @Parameter(description = "조회 개수 (기본 20)") int limit,
        @Parameter(description = "조회 시작 위치 (기본 0)") int offset
    );

    @Operation(
        summary = "마스터 상세 조회",
        description = "관리자 권한으로 비공개/미승인 마스터도 조회 가능합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "마스터 상세",
                      "statusCode": 200,
                      "data": {
                        "masterId": 1,
                        "userId": 10,
                        "displayName": "타로마스터홍길동",
                        "intro": "10년 경력의 타로 마스터입니다.",
                        "profileImageUrl": "https://...",
                        "specialties": ["LOVE", "CAREER"],
                        "careerYears": 10,
                        "status": "ONLINE",
                        "approvalStatus": "PENDING",
                        "isPublic": true,
                        "createdAt": "2026-04-01T00:00:00Z",
                        "updatedAt": "2026-04-28T00:00:00Z"
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "200", description = "마스터 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": false, "message": "마스터를 찾을 수 없습니다.", "statusCode": 201}
                    """)))
    })
    GlobalApiResponse<TaroMasterResponse> get(
        @Parameter(description = "마스터 ID") Long masterId
    );

    @Operation(
        summary = "마스터 승인/반려",
        description = """
            마스터 신청을 승인(APPROVED) 또는 반려(REJECTED)합니다.
            - 승인 시 해당 유저의 `isTaroMaster` 필드가 `true`로 변경됩니다.
            - 반려 시 `reason` 필드에 사유를 기입할 수 있습니다.

            **Request Body:**
            ```json
            {
              "approvalStatus": "APPROVED",
              "reason": null
            }
            ```
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "처리 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "마스터 승인 완료",
                      "statusCode": 200,
                      "data": {
                        "masterId": 1,
                        "approvalStatus": "APPROVED",
                        "displayName": "타로마스터홍길동"
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "200", description = "마스터 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": false, "message": "마스터를 찾을 수 없습니다.", "statusCode": 201}
                    """)))
    })
    GlobalApiResponse<?> approve(
        @Parameter(description = "마스터 ID") Long masterId,
        MasterApprovalRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
