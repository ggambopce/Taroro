package com.neocompany.taroro.domain.users.docs;

import org.springframework.http.ResponseEntity;

import com.neocompany.taroro.domain.users.dto.MeAuthResponseDto;
import com.neocompany.taroro.domain.users.dto.WithdrawRequestDto;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 우리 ApiResponse는 이름 충돌로 fully qualified name 사용
// io.swagger.v3.oas.annotations.responses.ApiResponse → @ApiResponse (어노테이션)
// com.neocompany.taroro.global.response.ApiResponse   → 메서드 반환 타입

@Tag(name = "Auth", description = "인증 관련 API (로그인 사용자 정보 조회, 로그아웃, 회원탈퇴)")
public interface UserControllerDocs {

    @Operation(
        summary = "내 정보 조회",
        description = "로그인된 유저의 기본 정보를 반환합니다. SID 쿠키가 필요합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.neocompany.taroro.global.response.ApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "로그인 사용자 정보",
                      "statusCode": 200,
                      "data": {
                        "email": "user@example.com",
                        "loginType": "normal",
                        "userName": "홍길동",
                        "userRole": "ROLE_USER",
                        "createdAt": "2026-01-01T00:00:00Z"
                      }
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 — SID 쿠키 없음 또는 만료", content = @Content)
    })
    ResponseEntity<com.neocompany.taroro.global.response.ApiResponse<MeAuthResponseDto>> me(
        @Parameter(hidden = true) PrincipalDetails principal
    );

    // -------------------------------------------------------------------------

    @Operation(
        summary = "로그아웃",
        description = "DB에서 세션을 삭제하고 SID 쿠키를 만료시킵니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "로그아웃 완료",
                      "statusCode": 200
                    }
                    """)
            )
        )
    })
    ResponseEntity<com.neocompany.taroro.global.response.ApiResponse<Void>> logout(
        HttpServletRequest req,
        HttpServletResponse res
    );

    // -------------------------------------------------------------------------

    @Operation(
        summary = "회원탈퇴",
        description = """
            계정을 탈퇴 처리합니다.
            - **일반 로그인(normal)**: `password` 필드 필수
            - **소셜 로그인(kakao/google/naver)**: `password` 없이 요청
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "탈퇴 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "회원탈퇴 완료",
                      "statusCode": 200
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "200",
            description = "유효성 실패 (statusCode: 201)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "현재 비밀번호가 필요합니다.",
                      "statusCode": 201
                    }
                    """)
            )
        )
    })
    ResponseEntity<com.neocompany.taroro.global.response.ApiResponse<Void>> withdraw(
        @Parameter(hidden = true) PrincipalDetails principal,
        @Parameter(description = "비밀번호 (일반 로그인만 필수, 소셜 로그인은 생략 가능)")
        WithdrawRequestDto body,
        HttpServletRequest request,
        HttpServletResponse response
    );
}
