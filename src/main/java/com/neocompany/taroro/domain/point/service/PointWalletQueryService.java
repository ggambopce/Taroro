package com.neocompany.taroro.domain.point.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.point.dto.response.PointLedgerResponse;
import com.neocompany.taroro.domain.point.dto.response.PointWalletResponse;
import com.neocompany.taroro.domain.point.entity.PointLedger;
import com.neocompany.taroro.domain.point.entity.PointWallet;
import com.neocompany.taroro.domain.point.repository.PointLedgerRepository;
import com.neocompany.taroro.domain.point.repository.PointWalletRepository;
import com.neocompany.taroro.global.dto.PageResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointWalletQueryService {

    private final PointWalletRepository walletRepository;
    private final PointLedgerRepository ledgerRepository;

    public PointWalletResponse getMyWallet(Long userId) {
        PointWallet wallet = walletRepository.findByUserId(userId).orElse(null);
        return PointWalletResponse.from(userId, wallet);
    }

    public PageResult<PointLedgerResponse> getMyLedger(Long userId, int limit, int offset) {
        Slice<PointLedger> slice = ledgerRepository.findByUserIdOrderByIdDesc(
                userId, PageRequest.of(offset / Math.max(limit, 1), limit));
        return PageResult.of(
                slice.getContent().stream().map(PointLedgerResponse::new).toList(),
                limit, offset);
    }
}
