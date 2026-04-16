package com.neocompany.taroro.domain.taromaster.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.taromaster.dto.response.TaroMasterResponse;
import com.neocompany.taroro.domain.taromaster.entity.ApprovalStatus;
import com.neocompany.taroro.domain.taromaster.entity.MasterStatus;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaroMasterQueryService {

    private final TaroMasterRepository masterRepository;

    public PageResult<TaroMasterResponse> getPublicMasters(
            String keyword, String status, int limit, int offset) {

        MasterStatus masterStatus = (status != null) ? MasterStatus.from(status) : null;

        Slice<TaroMaster> slice = masterRepository.findPublicMasters(
                ApprovalStatus.APPROVED,
                keyword,
                masterStatus,
                PageRequest.of(offset / limit, limit));

        return new PageResult<>(
                slice.getContent().stream().map(TaroMasterResponse::new).toList(),
                limit, offset, slice.hasNext());
    }

    public TaroMasterResponse getMaster(Long masterId, Long requesterId) {
        TaroMaster master = masterRepository.findById(masterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));

        boolean isAdmin = false; // 현재 ROLE_ADMIN 체크는 SecurityConfig에서 처리
        if (!master.isVisible(requesterId, isAdmin)) {
            throw new BusinessException(ErrorCode.MASTER_ACCESS_DENIED);
        }
        return new TaroMasterResponse(master);
    }

    public TaroMasterResponse getMyMaster(Long userId) {
        TaroMaster master = masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
        return new TaroMasterResponse(master);
    }
}
