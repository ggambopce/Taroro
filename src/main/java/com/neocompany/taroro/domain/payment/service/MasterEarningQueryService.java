package com.neocompany.taroro.domain.payment.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.payment.dto.response.MasterEarningLedgerResponse;
import com.neocompany.taroro.domain.payment.dto.response.MasterEarningWalletResponse;
import com.neocompany.taroro.domain.payment.entity.MasterEarningLedger;
import com.neocompany.taroro.domain.payment.entity.MasterEarningWallet;
import com.neocompany.taroro.domain.payment.repository.MasterEarningLedgerRepository;
import com.neocompany.taroro.domain.payment.repository.MasterEarningWalletRepository;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MasterEarningQueryService {

    private final MasterEarningWalletRepository walletRepository;
    private final MasterEarningLedgerRepository ledgerRepository;
    private final TaroMasterRepository masterRepository;

    public MasterEarningWalletResponse getMyEarnings(Long userId) {
        TaroMaster master = masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
        MasterEarningWallet wallet = walletRepository.findByMasterId(master.getMasterId()).orElse(null);
        return MasterEarningWalletResponse.from(master.getMasterId(), wallet);
    }

    public PageResult<MasterEarningLedgerResponse> getMyLedger(Long userId, int limit, int offset) {
        TaroMaster master = masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
        Slice<MasterEarningLedger> slice = ledgerRepository.findByMaster_MasterIdOrderByIdDesc(
                master.getMasterId(), PageRequest.of(offset / Math.max(limit, 1), limit));
        return PageResult.of(
                slice.getContent().stream().map(MasterEarningLedgerResponse::new).toList(),
                limit, offset);
    }
}
