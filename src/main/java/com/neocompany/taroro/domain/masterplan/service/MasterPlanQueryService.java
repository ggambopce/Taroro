package com.neocompany.taroro.domain.masterplan.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.masterplan.dto.response.MasterPlanResponse;
import com.neocompany.taroro.domain.masterplan.entity.MasterPlan;
import com.neocompany.taroro.domain.masterplan.repository.MasterPlanRepository;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MasterPlanQueryService {

    private final MasterPlanRepository planRepository;

    public PageResult<MasterPlanResponse> getPublicPlans(Long masterId, Boolean isActive,
                                                          int limit, int offset) {
        Slice<MasterPlan> slice = planRepository.findPublicPlans(
                masterId, isActive, PageRequest.of(offset / limit, limit));
        return PageResult.of(
                slice.getContent().stream().map(MasterPlanResponse::new).toList(),
                limit, offset);
    }

    public MasterPlanResponse getPlan(Long planId) {
        MasterPlan plan = planRepository.findByPlanIdAndDeletedFalse(planId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
        return new MasterPlanResponse(plan);
    }
}
