package com.neocompany.taroro.domain.taromaster.docs;

import org.springframework.web.multipart.MultipartFile;

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

    @Operation(
        summary = "마스터 등록 신청",
        description = """
            일반 사용자가 타로 마스터 등록을 신청합니다. 승인 상태는 PENDING으로 저장됩니다.

            **요청 형식: `multipart/form-data`**
            - `data` 파트 (application/json, 필수): 마스터 메타데이터 JSON
            - `profileImage` 파트 (image/*, 선택): 프로필 이미지 파일. 서버가 S3에 업로드 후 URL 저장.
              누락 시 `profileImageUrl = null` 로 저장됩니다.

            **정산 계좌 정보** (선택, `data` 파트 안에 포함):
            - `bankName`, `accountNumber`, `accountHolderName`, `phone`, `email` 5개 모두 입력 시 정산 계좌도 함께 등록됩니다.
            - 일부만 입력하거나 누락된 경우 정산 정보는 생성되지 않으며, 추후 `POST /api/master-auth/settlement` 로 별도 등록 가능합니다.
            - 등록된 계좌는 `isVerifiedAccount = false` 상태이며 출금 신청 전 PASS 인증 및 계좌 인증이 필요합니다.

            **`data` 파트 JSON 예시:**
            ```json
            {
              "displayName": "별빛타로",
              "intro": "10년 경력의 타로 마스터입니다.",
              "specialties": ["연애", "진로"],
              "careerYears": 10,
              "isPublic": true,
              "bankName": "국민은행",
              "accountNumber": "1234567890",
              "accountHolderName": "홍길동",
              "phone": "010-1234-5678",
              "email": "master@example.com"
            }
            ```

            **curl 예시:**
            ```bash
            curl -X POST /api/taro-masters \\
              -H "Cookie: SID=<세션>" \\
              -F 'data={"displayName":"별빛타로",...};type=application/json' \\
              -F 'profileImage=@./face.jpg;type=image/jpeg'
            ```
            """
    )
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
        @Parameter(description = "마스터 메타데이터 JSON", required = true) CreateTaroMasterRequest request,
        @Parameter(description = "프로필 이미지 파일 (image/*, 선택)") MultipartFile profileImage,
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

    @Operation(summary = "내 마스터 정보 조회", description = "로그인한 사용자의 마스터 정보를 조회합니다. SID 쿠키 필요.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 결과",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = {
                    @ExampleObject(name = "성공", value = """
                        {
                          "success": true,
                          "message": "마스터 정보 조회 성공",
                          "statusCode": 200,
                          "data": {
                            "masterId": 22,
                            "userId": 10,
                            "displayName": "별빛타로",
                            "intro": "10년 경력의 타로 마스터입니다.",
                            "profileImageUrl": "https://example.com/image.jpg",
                            "specialties": ["연애", "진로"],
                            "careerYears": 10,
                            "status": "ONLINE",
                            "approvalStatus": "APPROVED",
                            "isPublic": true
                          }
                        }
                        """),
                    @ExampleObject(name = "마스터 미등록", value = """
                        {
                          "success": false,
                          "message": "마스터 정보가 없습니다.",
                          "statusCode": 201
                        }
                        """)
                }))
    })
    GlobalApiResponse<TaroMasterResponse> getMe(
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(summary = "마스터 상세 조회", description = "특정 마스터의 공개 프로필을 조회합니다. 비공개/미승인 상태는 본인만 조회 가능합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 결과",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = {
                    @ExampleObject(name = "성공", value = """
                        {
                          "success": true,
                          "message": "마스터 정보 조회 성공",
                          "statusCode": 200,
                          "data": {
                            "masterId": 22,
                            "displayName": "별빛타로",
                            "status": "ONLINE",
                            "approvalStatus": "APPROVED",
                            "isPublic": true
                          }
                        }
                        """),
                    @ExampleObject(name = "접근 불가 (비공개)", value = """
                        {
                          "success": false,
                          "message": "접근 권한이 없습니다.",
                          "statusCode": 201
                        }
                        """)
                }))
    })
    GlobalApiResponse<TaroMasterResponse> get(
        Long masterId,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(
        summary = "마스터 정보 수정",
        description = """
            로그인한 마스터가 본인 프로필을 수정합니다. SID 쿠키 필요.

            **요청 형식: `multipart/form-data`**
            - `data` 파트 (application/json, 필수): 변경할 필드만 포함. null/미포함 필드는 기존 값 유지.
            - `profileImage` 파트 (image/*, 선택): 이미지 교체 시에만 포함. 누락 시 기존 이미지 URL 유지.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "처리 결과",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "성공", value = """
                        { "success": true, "message": "마스터 정보 수정 완료", "statusCode": 200 }
                        """),
                    @ExampleObject(name = "마스터 미등록", value = """
                        { "success": false, "message": "마스터 정보가 없습니다.", "statusCode": 201 }
                        """)
                }))
    })
    GlobalApiResponse<?> update(
        @Parameter(description = "변경할 마스터 필드 JSON", required = true) UpdateTaroMasterRequest request,
        @Parameter(description = "교체할 프로필 이미지 파일 (image/*, 선택)") MultipartFile profileImage,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
