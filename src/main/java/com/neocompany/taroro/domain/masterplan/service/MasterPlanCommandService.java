package com.neocompany.taroro.domain.masterplan.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.masterplan.dto.request.CreateMasterPlanRequest;
import com.neocompany.taroro.domain.masterplan.dto.request.UpdateMasterPlanRequest;
import com.neocompany.taroro.domain.masterplan.entity.MasterPlan;
import com.neocompany.taroro.domain.masterplan.repository.MasterPlanRepository;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MasterPlanCommandService {

    private final MasterPlanRepository planRepository;
    private final TaroMasterRepository masterRepository;

    public Long create(Long userId, CreateMasterPlanRequest request) {
        TaroMaster master = requireMaster(userId);

        MasterPlan plan = MasterPlan.builder()
                .masterId(master.getMasterId())
                .planName(request.getPlanName())
                .planDescription(request.getPlanDescription())
                .counselingMinutes(request.getCounselingMinutes())
                .price(request.getPrice())
                .discountRate(request.getDiscountRate() != null ? request.getDiscountRate() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .build();

        return planRepository.save(plan).getPlanId();
    }

    public Long update(Long userId, Long planId, UpdateMasterPlanRequest request) {
        TaroMaster master = requireMaster(userId);
        MasterPlan plan = getOwnedPlan(planId, master.getMasterId());
        plan.update(request.getPlanName(), request.getPlanDescription(),
                request.getCounselingMinutes(), request.getPrice(),
                request.getDiscountRate(), request.getIsActive(), request.getIsPublic());
        return plan.getPlanId();
    }

    public void delete(Long userId, Long planId) {
        TaroMaster master = requireMaster(userId);
        MasterPlan plan = getOwnedPlan(planId, master.getMasterId());
        plan.softDelete();
    }

    private TaroMaster requireMaster(Long userId) {
        return masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
    }

    private MasterPlan getOwnedPlan(Long planId, Long masterId) {
        MasterPlan plan = planRepository.findByPlanIdAndDeletedFalse(planId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
        if (!plan.isOwnedBy(masterId)) {
            throw new BusinessException(ErrorCode.PLAN_ACCESS_DENIED);
        }
        return plan;
    }
}
