package com.neocompany.taroro.domain.point.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.point.docs.PointChargeControllerDocs;
import com.neocompany.taroro.domain.point.dto.PointChargeConfirmRequestDto;
import com.neocompany.taroro.domain.point.dto.PointChargeReadyRequestDto;
import com.neocompany.taroro.domain.point.dto.PointChargeReadyResponseDto;
import com.neocompany.taroro.domain.point.service.PointChargeService;
import com.neocompany.taroro.domain.users.User;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/point/charge")
@RequiredArgsConstructor
public class PointChargeController implements PointChargeControllerDocs {

    private final PointChargeService pointChargeService;

    @Override
    @PostMapping("/toss/ready")
    public ResponseEntity<GlobalApiResponse<PointChargeReadyResponseDto>> ready(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody @Valid PointChargeReadyRequestDto body
    ) {
        User user = principal.getUser();
        PointChargeReadyResponseDto result = pointChargeService.ready(user, body);
        return ResponseEntity.ok(GlobalApiResponse.ok("충전 준비 완료", result));
    }

    @Override
    @PostMapping("/toss/confirm")
    public ResponseEntity<GlobalApiResponse<Void>> confirm(
            @RequestBody @Valid PointChargeConfirmRequestDto body
    ) {
        pointChargeService.confirm(body);
        return ResponseEntity.ok(GlobalApiResponse.ok("포인트 충전 완료", null));
    }
}
