package com.neocompany.taroro.domain.masterauth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.masterauth.dto.request.CreateSettlementRequest;
import com.neocompany.taroro.domain.masterauth.dto.request.UpdateSettlementRequest;
import com.neocompany.taroro.domain.masterauth.dto.response.SettlementResponse;
import com.neocompany.taroro.domain.masterauth.entity.MasterSettlement;
import com.neocompany.taroro.domain.masterauth.repository.MasterSettlementRepository;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MasterSettlementService {

    private final MasterSettlementRepository settlementRepository;
    private final TaroMasterRepository masterRepository;

    @Transactional(readOnly = true)
    public SettlementResponse getMy(Long userId) {
        TaroMaster master = requireMaster(userId);
        MasterSettlement settlement = settlementRepository.findByMasterId(master.getMasterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SETTLEMENT_NOT_FOUND));
        return new SettlementResponse(settlement);
    }

    public Long create(Long userId, CreateSettlementRequest request) {
        TaroMaster master = requireMaster(userId);

        if (settlementRepository.existsByMasterId(master.getMasterId())) {
            throw new BusinessException(ErrorCode.SETTLEMENT_ALREADY_EXISTS);
        }

        MasterSettlement settlement = MasterSettlement.builder()
                .masterId(master.getMasterId())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountHolderName(request.getAccountHolderName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();

        return settlementRepository.save(settlement).getSettlementId();
    }

    public void update(Long userId, UpdateSettlementRequest request) {
        TaroMaster master = requireMaster(userId);
        MasterSettlement settlement = settlementRepository.findByMasterId(master.getMasterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SETTLEMENT_NOT_FOUND));
        settlement.update(request.getBankName(), request.getAccountNumber(),
                request.getAccountHolderName(), request.getPhone(), request.getEmail());
    }

    private TaroMaster requireMaster(Long userId) {
        return masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
    }
}
