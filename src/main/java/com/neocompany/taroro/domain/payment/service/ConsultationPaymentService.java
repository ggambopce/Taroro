package com.neocompany.taroro.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.masterplan.entity.MasterPlan;
import com.neocompany.taroro.domain.masterplan.repository.MasterPlanRepository;
import com.neocompany.taroro.domain.payment.entity.ConsultationPayment;
import com.neocompany.taroro.domain.payment.entity.MasterEarningLedger;
import com.neocompany.taroro.domain.payment.entity.MasterEarningWallet;
import com.neocompany.taroro.domain.payment.enums.MasterEarningType;
import com.neocompany.taroro.domain.payment.repository.ConsultationPaymentRepository;
import com.neocompany.taroro.domain.payment.repository.MasterEarningLedgerRepository;
import com.neocompany.taroro.domain.payment.repository.MasterEarningWalletRepository;
import com.neocompany.taroro.domain.point.entity.PointLedger;
import com.neocompany.taroro.domain.point.entity.PointWallet;
import com.neocompany.taroro.domain.point.enums.PointLedgerType;
import com.neocompany.taroro.domain.point.repository.PointLedgerRepository;
import com.neocompany.taroro.domain.point.repository.PointWalletRepository;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationPaymentService {

    private final ConsultationPaymentRepository paymentRepository;
    private final MasterEarningWalletRepository earningWalletRepository;
    private final MasterEarningLedgerRepository earningLedgerRepository;
    private final PointWalletRepository pointWalletRepository;
    private final PointLedgerRepository pointLedgerRepository;
    private final MasterPlanRepository planRepository;
    private final TaroMasterRepository masterRepository;

    /**
     * 방 start 시점에 호출되는 사전 결제 처리.
     * - 사용자 PointWallet 락 + 차감
     * - 마스터 EarningWallet 락 + 적립 (수수료 차감)
     * - PointLedger + MasterEarningLedger 기록
     * - ConsultationPayment 1행 저장
     *
     * room.planId == null 이면 무료/테스트 방으로 간주하여 결제 건너뜀.
     */
    @Transactional
    public void chargeForRoom(Room room) {
        if (room.getPlanId() == null) {
            log.debug("[Payment] skip — roomId={} has no planId (free/test)", room.getId());
            return;
        }
        if (paymentRepository.existsByRoomId(room.getId())) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
        }

        MasterPlan plan = planRepository.findById(room.getPlanId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
        long price = plan.getDiscountedPrice();

        Long userId = room.getUserId();
        Long masterUserId = room.getMasterId();

        // 1. 사용자 잔액 락 + 차감
        PointWallet userWallet = pointWalletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSUFFICIENT_POINTS));
        if (userWallet.getBalance() < price) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINTS);
        }
        userWallet.deduct(price);
        pointLedgerRepository.save(
                PointLedger.of(userWallet, -price, PointLedgerType.USE, "room", room.getId()));

        // 2. 수수료 계산 + 마스터 적립
        TaroMaster master = masterRepository.findByUserId(masterUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
        int rate = master.getCommissionRate();
        long fee = price * rate / 100;
        long net = price - fee;

        MasterEarningWallet mw = earningWalletRepository.findByMasterIdForUpdate(master.getMasterId())
                .orElseGet(() -> earningWalletRepository.save(MasterEarningWallet.empty(master)));
        mw.credit(net);
        earningLedgerRepository.save(
                MasterEarningLedger.of(mw, net, MasterEarningType.EARN, "room", room.getId()));

        // 3. 결제 기록
        paymentRepository.save(ConsultationPayment.completed(
                room, userId, master.getMasterId(), plan.getPlanId(),
                price, fee, net, rate));

        log.info("[Payment] charged roomId={} userId={} masterId={} price={} fee={} net={}",
                room.getId(), userId, master.getMasterId(), price, fee, net);
    }

    /**
     * 관리자가 호출하는 환불 처리.
     */
    @Transactional
    public void refund(Long paymentId) {
        ConsultationPayment p = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        if (p.getStatus() == com.neocompany.taroro.domain.payment.enums.PaymentStatus.REFUNDED) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_REFUNDED);
        }

        // 사용자 환급
        PointWallet uw = pointWalletRepository.findByUserIdForUpdate(p.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자 지갑을 찾을 수 없습니다."));
        uw.credit(p.getGrossAmount());
        pointLedgerRepository.save(
                PointLedger.of(uw, p.getGrossAmount(), PointLedgerType.REFUND,
                        "consultation_payment", p.getId()));

        // 마스터 차감
        MasterEarningWallet mw = earningWalletRepository.findByMasterIdForUpdate(p.getMasterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EARNING_WALLET_NOT_FOUND));
        if (mw.getBalance() < p.getNetToMaster()) {
            throw new BusinessException(ErrorCode.MASTER_BALANCE_INSUFFICIENT_FOR_REFUND);
        }
        mw.deduct(p.getNetToMaster());
        earningLedgerRepository.save(
                MasterEarningLedger.of(mw, -p.getNetToMaster(), MasterEarningType.REFUND_DEDUCT,
                        "consultation_payment", p.getId()));

        p.markRefunded();

        log.info("[Payment] refunded paymentId={} userId={} masterId={} gross={}",
                p.getId(), p.getUserId(), p.getMasterId(), p.getGrossAmount());
    }
}
