package com.neocompany.taroro.domain.withdrawal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.masterauth.entity.MasterSettlement;
import com.neocompany.taroro.domain.masterauth.entity.MasterVerification;
import com.neocompany.taroro.domain.masterauth.repository.MasterSettlementRepository;
import com.neocompany.taroro.domain.masterauth.repository.MasterVerificationRepository;
import com.neocompany.taroro.domain.payment.entity.MasterEarningLedger;
import com.neocompany.taroro.domain.payment.entity.MasterEarningWallet;
import com.neocompany.taroro.domain.payment.enums.MasterEarningType;
import com.neocompany.taroro.domain.payment.repository.MasterEarningLedgerRepository;
import com.neocompany.taroro.domain.payment.repository.MasterEarningWalletRepository;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.domain.withdrawal.entity.WithdrawalRequest;
import com.neocompany.taroro.domain.withdrawal.enums.WithdrawalStatus;
import com.neocompany.taroro.domain.withdrawal.repository.WithdrawalRequestRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalCommandService {

    private final WithdrawalRequestRepository withdrawalRepository;
    private final MasterEarningWalletRepository earningWalletRepository;
    private final MasterEarningLedgerRepository earningLedgerRepository;
    private final TaroMasterRepository masterRepository;
    private final MasterSettlementRepository settlementRepository;
    private final MasterVerificationRepository verificationRepository;

    /**
     * 마스터 출금 신청 — 잔액 즉시 차감하여 잠금 처리.
     */
    @Transactional
    public Long requestWithdrawal(Long userId, long amount) {
        if (amount < 1) throw new BusinessException(ErrorCode.WITHDRAWAL_AMOUNT_INVALID);

        TaroMaster master = masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));

        MasterSettlement st = settlementRepository.findByMasterId(master.getMasterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SETTLEMENT_NOT_FOUND));
        MasterVerification v = verificationRepository.findByMasterId(master.getMasterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_NOT_FOUND));
        if (!v.isPassVerified()) {
            throw new BusinessException(ErrorCode.PASS_VERIFICATION_FAILED, "PASS 본인인증이 필요합니다.");
        }
        if (!st.isVerifiedAccount()) {
            throw new BusinessException(ErrorCode.SETTLEMENT_NOT_VERIFIED);
        }

        MasterEarningWallet mw = earningWalletRepository.findByMasterIdForUpdate(master.getMasterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.WITHDRAWAL_INSUFFICIENT));
        if (mw.getBalance() < amount) {
            throw new BusinessException(ErrorCode.WITHDRAWAL_INSUFFICIENT);
        }
        mw.lockForWithdrawal(amount);

        WithdrawalRequest req = WithdrawalRequest.create(master, amount,
                st.getBankName(), st.getAccountNumber(), st.getAccountHolderName());
        Long id = withdrawalRepository.save(req).getId();
        log.info("[Withdrawal] requested id={} masterId={} amount={}", id, master.getMasterId(), amount);
        return id;
    }

    /**
     * 관리자 승인 — 외부 송금 직전 단계. 잔액은 신청 시 이미 차감되어 있음.
     */
    @Transactional
    public void approve(Long withdrawalId, Long adminUserId) {
        WithdrawalRequest req = findPending(withdrawalId);
        req.approve(adminUserId);
        log.info("[Withdrawal] approved id={} by adminId={}", withdrawalId, adminUserId);
    }

    /**
     * 관리자 거절 — 잠금된 잔액을 복구.
     */
    @Transactional
    public void reject(Long withdrawalId, Long adminUserId, String reason) {
        WithdrawalRequest req = findPending(withdrawalId);

        MasterEarningWallet mw = earningWalletRepository
                .findByMasterIdForUpdate(req.getMaster().getMasterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EARNING_WALLET_NOT_FOUND));
        mw.restoreLocked(req.getAmount());
        earningLedgerRepository.save(MasterEarningLedger.of(
                mw, req.getAmount(), MasterEarningType.ADJUSTMENT,
                "withdrawal_request_rejected", req.getId()));

        req.reject(adminUserId, reason);
        log.info("[Withdrawal] rejected id={} reason={}", withdrawalId, reason);
    }

    /**
     * 관리자 완료 표시 — 외부 송금 완료 후 호출. 잔액 변화 없음, 원장에 WITHDRAW 기록.
     */
    @Transactional
    public void complete(Long withdrawalId, Long adminUserId) {
        WithdrawalRequest req = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WITHDRAWAL_NOT_FOUND));
        if (req.getStatus() != WithdrawalStatus.APPROVED) {
            throw new BusinessException(ErrorCode.WITHDRAWAL_INVALID_STATUS,
                    "승인된 신청만 완료 처리할 수 있습니다.");
        }

        MasterEarningWallet mw = earningWalletRepository
                .findByMasterIdForUpdate(req.getMaster().getMasterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EARNING_WALLET_NOT_FOUND));
        // 잔액은 신청 시 이미 차감됨. 원장 기록만 남김.
        earningLedgerRepository.save(MasterEarningLedger.of(
                mw, -req.getAmount(), MasterEarningType.WITHDRAW,
                "withdrawal_request", req.getId()));

        req.complete();
        log.info("[Withdrawal] completed id={} by adminId={}", withdrawalId, adminUserId);
    }

    private WithdrawalRequest findPending(Long id) {
        WithdrawalRequest req = withdrawalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WITHDRAWAL_NOT_FOUND));
        if (req.getStatus() != WithdrawalStatus.PENDING) {
            throw new BusinessException(ErrorCode.WITHDRAWAL_INVALID_STATUS);
        }
        return req;
    }
}
