package com.neocompany.taroro.domain.admin.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.admin.docs.AdminTaroCardSetControllerDocs;
import com.neocompany.taroro.domain.admin.service.AdminTaroMasterService;
import com.neocompany.taroro.domain.tarocardset.dto.request.CreateTaroCardSetRequest;
import com.neocompany.taroro.domain.tarocardset.dto.request.UpdateTaroCardSetRequest;
import com.neocompany.taroro.domain.tarocardset.dto.response.TaroCardSetResponse;
import com.neocompany.taroro.domain.tarocardset.service.TaroCardSetCommandService;
import com.neocompany.taroro.domain.tarocardset.service.TaroCardSetQueryService;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AdminTaroCardSetController implements AdminTaroCardSetControllerDocs {

    private final TaroCardSetQueryService queryService;
    private final TaroCardSetCommandService commandService;
    private final AdminTaroMasterService adminMasterService;

    @Override
    @GetMapping("/api/admin/taro-card-sets")
    public GlobalApiResponse<PageResult<TaroCardSetResponse>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long masterId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        PageResult<TaroCardSetResponse> result = queryService.getPublicSets(keyword, masterId, isActive, limit, offset);

        List<Long> masterIds = result.items().stream()
                .map(TaroCardSetResponse::getMasterId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> nameMap = adminMasterService.getDisplayNameMap(masterIds);

        List<TaroCardSetResponse> items = result.items().stream()
                .map(item -> item.withMasterName(nameMap.get(item.getMasterId())))
                .collect(Collectors.toList());

        return GlobalApiResponse.ok("타로 카드 세트 목록 조회 성공",
                new PageResult<>(items, result.limit(), result.offset(), result.hasNext()));
    }

    @Override
    @GetMapping("/api/admin/taro-card-sets/{setId}")
    public GlobalApiResponse<TaroCardSetResponse> get(@PathVariable Long setId) {
        return GlobalApiResponse.ok("타로 카드 세트 조회 성공",
                queryService.getSetForAdmin(setId));
    }

    @Override
    @GetMapping("/api/admin/taro-masters/me/card-sets")
    public GlobalApiResponse<PageResult<TaroCardSetResponse>> getMySets(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @AuthenticationPrincipal PrincipalDetails principal) {
        return GlobalApiResponse.ok("내 카드 세트 목록 조회 성공",
                queryService.getMySets(principal.getUser().getUserId(), limit, offset));
    }

    @Override
    @PostMapping("/api/admin/taro-masters/me/card-sets")
    public GlobalApiResponse<?> create(
            @RequestBody CreateTaroCardSetRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long setId = commandService.create(principal.getUser().getUserId(), request);
        return GlobalApiResponse.ok("내 카드 세트 등록 성공", Map.of("setId", setId));
    }

    @Override
    @PatchMapping("/api/admin/taro-masters/me/card-sets/{masterCardSetId}")
    public GlobalApiResponse<?> update(
            @PathVariable Long masterCardSetId,
            @RequestBody UpdateTaroCardSetRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long setId = commandService.update(principal.getUser().getUserId(), masterCardSetId, request);
        return GlobalApiResponse.ok("내 카드 세트 수정 성공", Map.of("setId", setId));
    }

    @Override
    @DeleteMapping("/api/admin/taro-masters/me/card-sets/{masterCardSetId}")
    public GlobalApiResponse<?> delete(
            @PathVariable Long masterCardSetId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        commandService.delete(principal.getUser().getUserId(), masterCardSetId);
        return GlobalApiResponse.ok("내 카드 세트 삭제 성공", null);
    }
}
