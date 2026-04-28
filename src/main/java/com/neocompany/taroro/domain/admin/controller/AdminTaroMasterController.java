package com.neocompany.taroro.domain.admin.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.admin.docs.AdminTaroMasterControllerDocs;
import com.neocompany.taroro.domain.admin.dto.request.MasterApprovalRequest;
import com.neocompany.taroro.domain.admin.service.AdminTaroMasterService;
import com.neocompany.taroro.domain.taromaster.dto.response.TaroMasterResponse;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/taro-masters")
@RequiredArgsConstructor
public class AdminTaroMasterController implements AdminTaroMasterControllerDocs {

    private final AdminTaroMasterService adminService;

    @Override
    @GetMapping
    public GlobalApiResponse<PageResult<TaroMasterResponse>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return GlobalApiResponse.ok("마스터 목록 조회 성공",
                adminService.getAllMasters(keyword, status, limit, offset));
    }

    @Override
    @GetMapping("/{masterId}")
    public GlobalApiResponse<TaroMasterResponse> get(@PathVariable Long masterId) {
        return GlobalApiResponse.ok("마스터 조회 성공",
                adminService.getMasterForAdmin(masterId));
    }

    @Override
    @PatchMapping("/{masterId}/approval")
    public GlobalApiResponse<?> approve(
            @PathVariable Long masterId,
            @RequestBody MasterApprovalRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        TaroMasterResponse result = adminService.approve(masterId, request);
        return GlobalApiResponse.ok("마스터 승인 상태 변경 성공",
                Map.of("masterId", result.getMasterId(), "approvalStatus", result.getApprovalStatus()));
    }
}
