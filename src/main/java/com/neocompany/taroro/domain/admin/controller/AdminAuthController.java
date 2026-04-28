package com.neocompany.taroro.domain.admin.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.admin.docs.AdminAuthControllerDocs;
import com.neocompany.taroro.domain.users.UserService;
import com.neocompany.taroro.domain.users.dto.LoginRequestDto;
import com.neocompany.taroro.domain.users.dto.MeAuthResponseDto;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController implements AdminAuthControllerDocs {

    private final UserService userService;

    @Override
    @PostMapping("/auth/login")
    public GlobalApiResponse<?> login(
            @RequestBody @Valid LoginRequestDto request,
            HttpServletResponse response) {
        userService.adminLogin(request, response);
        return GlobalApiResponse.ok("로그인 성공", null);
    }

    @Override
    @GetMapping("/me")
    public GlobalApiResponse<MeAuthResponseDto> me(
            @AuthenticationPrincipal PrincipalDetails principal) {
        MeAuthResponseDto result = userService.getMeAuth(principal.getUser());
        return GlobalApiResponse.ok("로그인 관리자 정보", result);
    }
}
