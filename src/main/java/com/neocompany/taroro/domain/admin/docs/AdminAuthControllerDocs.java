package com.neocompany.taroro.domain.admin.docs;

import com.neocompany.taroro.domain.users.dto.LoginRequestDto;
import com.neocompany.taroro.domain.users.dto.MeAuthResponseDto;
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
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Admin - Auth", description = "관리자 인증 API")
public interface AdminAuthControllerDocs {

    @Operation(
        summary = "관리자 로그인",
        description = """
            이메일/비밀번호로 로그인합니다.
            - ROLE_ADMIN 권한이 없는 계정은 401을 반환합니다.
            - 성공 시 `SID` HttpOnly 쿠키가 발급됩니다 (1년 만료).
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "로그인 성공",
                      "statusCode": 200
                    }
                    """))),
        @ApiResponse(responseCode = "200", description = "관리자 권한 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "관리자 권한이 없습니다.",
                      "statusCode": 201
                    }
                    """))),
        @ApiResponse(responseCode = "200", description = "이메일/비밀번호 불일치",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "비밀번호가 일치하지 않습니다.",
                      "statusCode": 201
                    }
                    """)))
    })
    GlobalApiResponse<?> login(LoginRequestDto request, HttpServletResponse response);

    @Operation(
        summary = "관리자 정보 조회",
        description = "현재 로그인된 관리자의 기본 정보를 반환합니다. SID 쿠키 필요."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "로그인 관리자 정보",
                      "statusCode": 200,
                      "result": {
                        "email": "admin@example.com",
                        "loginType": "normal",
                        "userName": "관리자",
                        "userRole": "ROLE_ADMIN",
                        "createdAt": "2026-01-01T00:00:00Z"
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "미인증",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "인증이 필요합니다.",
                      "statusCode": 401
                    }
                    """)))
    })
    GlobalApiResponse<MeAuthResponseDto> me(
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
