package com.neocompany.taroro.domain.withdrawal.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.domain.withdrawal.dto.response.WithdrawalResponse;
import com.neocompany.taroro.domain.withdrawal.entity.WithdrawalRequest;
import com.neocompany.taroro.domain.withdrawal.enums.WithdrawalStatus;
import com.neocompany.taroro.domain.withdrawal.repository.WithdrawalRequestRepository;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WithdrawalQueryService {

    private final WithdrawalRequestRepository withdrawalRepository;
    private final TaroMasterRepository masterRepository;

    public PageResult<WithdrawalResponse> getMyWithdrawals(Long userId, int limit, int offset) {
        TaroMaster master = masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
        Slice<WithdrawalRequest> slice = withdrawalRepository.findByMaster_MasterIdOrderByIdDesc(
                master.getMasterId(), PageRequest.of(offset / Math.max(limit, 1), limit));
        return PageResult.of(
                slice.getContent().stream().map(WithdrawalResponse::new).toList(),
                limit, offset);
    }

    public WithdrawalResponse getMyWithdrawal(Long userId, Long withdrawalId) {
        TaroMaster master = masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
        WithdrawalRequest req = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WITHDRAWAL_NOT_FOUND));
        if (!req.getMaster().getMasterId().equals(master.getMasterId())) {
            throw new BusinessException(ErrorCode.WITHDRAWAL_NOT_FOUND);
        }
        return new WithdrawalResponse(req);
    }

    public PageResult<WithdrawalResponse> getAll(WithdrawalStatus status, int limit, int offset) {
        Slice<WithdrawalRequest> slice = withdrawalRepository.findAllByStatus(
                status, PageRequest.of(offset / Math.max(limit, 1), limit));
        return PageResult.of(
                slice.getContent().stream().map(WithdrawalResponse::new).toList(),
                limit, offset);
    }

    public WithdrawalResponse getOne(Long id) {
        return new WithdrawalResponse(withdrawalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WITHDRAWAL_NOT_FOUND)));
    }
}
