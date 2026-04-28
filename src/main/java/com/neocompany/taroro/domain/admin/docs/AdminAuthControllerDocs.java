package com.neocompany.taroro.domain.admin.docs;

import com.neocompany.taroro.domain.users.dto.LoginRequestDto;
import com.neocompany.taroro.domain.users.dto.MeAuthResponseDto;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Admin - Auth", description = "관리자 인증 API")
public interface AdminAuthControllerDocs {

    @Operation(summary = "관리자 로그인", description = "이메일/비밀번호로 로그인합니다. ROLE_ADMIN 권한이 없으면 401을 반환합니다. 성공 시 SID 쿠키가 발급됩니다.")
    GlobalApiResponse<?> login(LoginRequestDto request, HttpServletResponse response);

    @Operation(summary = "관리자 정보 조회", description = "현재 로그인된 관리자의 정보를 반환합니다.")
    GlobalApiResponse<MeAuthResponseDto> me(
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
