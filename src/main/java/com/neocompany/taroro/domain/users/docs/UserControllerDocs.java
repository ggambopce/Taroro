package com.neocompany.taroro.domain.users.docs;

import org.springframework.http.ResponseEntity;

import com.neocompany.taroro.domain.email.EmailRequestDto;
import com.neocompany.taroro.domain.email.EmailVerifyRequestDto;
import com.neocompany.taroro.domain.users.dto.LoginRequestDto;
import com.neocompany.taroro.domain.users.dto.MeAuthResponseDto;
import com.neocompany.taroro.domain.users.dto.ResetPasswordRequestDto;
import com.neocompany.taroro.domain.users.dto.SignupRequestDto;
import com.neocompany.taroro.domain.users.dto.WithdrawRequestDto;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Auth", description = "인증/회원가입/이메일 인증 관련 API")
public interface UserControllerDocs {

    @Operation(
        summary = "일반 로그인",
        description = "이메일과 비밀번호로 로그인하고 SID 쿠키를 발급합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "로그인 성공",
                      "statusCode": 200,
                      "data": null
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "비밀번호 불일치",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "비밀번호가 일치하지 않습니다.",
                      "statusCode": 401,
                      "data": null
                    }
                    """)
            )
        )
    })
    ResponseEntity<GlobalApiResponse<?>> login(
        LoginRequestDto req,
        HttpServletResponse res
    );

    @Operation(
        summary = "일반 회원가입",
        description = """
            일반 회원가입을 진행합니다.
            - 이메일 형식 유효성 필요
            - 비밀번호/비밀번호 확인 일치 필요
            - 이메일 인증 완료 필요
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "회원가입 성공",
                      "statusCode": 200,
                      "data": null
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "회원가입 실패",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "이메일 인증이 필요합니다.",
                      "statusCode": 400,
                      "data": null
                    }
                    """)
            )
        )
    })
    ResponseEntity<GlobalApiResponse<?>> signup(
        SignupRequestDto req
    );

    @Operation(
        summary = "이메일 중복확인",
        description = "입력한 이메일이 이미 가입된 이메일인지 확인합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "이미 사용중",
                        value = """
                            {
                              "success": true,
                              "message": "이미 사용중인 이메일입니다.",
                              "statusCode": 200,
                              "data": true
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "사용 가능",
                        value = """
                            {
                              "success": true,
                              "message": "사용가능 이메일입니다.",
                              "statusCode": 200,
                              "data": false
                            }
                            """
                    )
                }
            )
        )
    })
    ResponseEntity<GlobalApiResponse<Boolean>> dupEmail(
        EmailRequestDto req
    );

    @Operation(
        summary = "이메일 인증코드 발송",
        description = "입력한 이메일로 6자리 인증코드를 전송합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "이메일 전송 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "이메일 코드 발송 성공",
                      "statusCode": 200,
                      "data": null
                    }
                    """)
            )
        )
    })
    ResponseEntity<GlobalApiResponse<?>> send(
        EmailRequestDto req
    );

    @Operation(
        summary = "이메일 인증코드 확인",
        description = "발송한 6자리 인증코드를 검증합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "검증 완료",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "인증 성공",
                        value = """
                            {
                              "success": true,
                              "message": "인증을 성공했습니다.",
                              "statusCode": 200,
                              "data": true
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "인증 실패",
                        value = """
                            {
                              "success": true,
                              "message": "코드가 일치하지 않습니다.",
                              "statusCode": 200,
                              "data": false
                            }
                            """
                    )
                }
            )
        )
    })
    ResponseEntity<GlobalApiResponse<Boolean>> verify(
        EmailVerifyRequestDto req
    );

    @Operation(
        summary = "비밀번호 재설정",
        description = "이메일 인증이 완료된 사용자의 비밀번호를 변경합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "비밀번호 변경 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "비밀번호 변경 성공",
                      "statusCode": 200,
                      "data": null
                    }
                    """)
            )
        )
    })
    ResponseEntity<GlobalApiResponse<?>> resetPassword(
        ResetPasswordRequestDto req
    );

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
                schema = @Schema(implementation = GlobalApiResponse.class),
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
        @ApiResponse(
            responseCode = "401",
            description = "SID 쿠키 없음 또는 만료",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "인증이 필요합니다.",
                      "statusCode": 401,
                      "data": null
                    }
                    """)
            )
        )
    })
    ResponseEntity<GlobalApiResponse<MeAuthResponseDto>> me(
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(
        summary = "로그아웃",
        description = "DB 세션을 삭제하고 SID 쿠키를 만료시킵니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "로그아웃 완료",
                      "statusCode": 200,
                      "data": null
                    }
                    """)
            )
        )
    })
    ResponseEntity<GlobalApiResponse<Void>> logout(
        HttpServletRequest req,
        HttpServletResponse res
    );

    @Operation(
        summary = "회원탈퇴",
        description = """
            계정을 탈퇴 처리합니다.
            - 일반 로그인(normal): password 필드 필수
            - 소셜 로그인(kakao/google/naver): password 없이 요청 가능
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "탈퇴 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "회원탈퇴 완료",
                      "statusCode": 200,
                      "data": null
                    }
                    """)
            )
        )
    })
    ResponseEntity<GlobalApiResponse<Void>> withdraw(
        @Parameter(hidden = true) PrincipalDetails principal,
        WithdrawRequestDto body,
        HttpServletRequest request,
        HttpServletResponse response
    );
}