package com.neocompany.taroro.domain.point.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.point.dto.PointChargeConfirmRequestDto;
import com.neocompany.taroro.domain.point.dto.PointChargeReadyRequestDto;
import com.neocompany.taroro.domain.point.dto.PointChargeReadyResponseDto;
import com.neocompany.taroro.domain.point.entity.PointCharge;
import com.neocompany.taroro.domain.point.enums.PointChargeStatus;
import com.neocompany.taroro.domain.point.repository.PointChargeRepository;
import com.neocompany.taroro.domain.toss.TossPaymentsClient;
import com.neocompany.taroro.domain.toss.TossPaymentsClient.TossConfirmResponse;
import com.neocompany.taroro.domain.users.User;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointChargeService {

    private final PointChargeRepository pointChargeRepository;
    private final TossPaymentsClient tossPaymentsClient;
    private final PointChargeApplier pointChargeApplier;

    @Transactional
    public PointChargeReadyResponseDto ready(User user, PointChargeReadyRequestDto req) {
        String orderId = UUID.randomUUID().toString();
        PointCharge charge = pointChargeRepository.save(PointCharge.builder()
                .user(user)
                .orderId(orderId)
                .amount(req.getPointAmount())
                .status(PointChargeStatus.READY)
                .build());
        return new PointChargeReadyResponseDto(charge.getId(), orderId, charge.getAmount());
    }

    // NOTE: NOT @Transactional — Toss API call must happen outside DB transaction
    public void confirm(PointChargeConfirmRequestDto req) {
        PointCharge charge = pointChargeRepository.findByOrderId(req.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

        if (charge.getStatus() == PointChargeStatus.PAID) {
            // Idempotent — already processed
            return;
        }

        // Call Toss API (outside DB transaction)
        TossConfirmResponse tossResponse = tossPaymentsClient.confirm(
                req.getPaymentKey(),
                req.getOrderId(),
                charge.getAmount()
        );

        Instant approvedAt = tossResponse.getApprovedAt() != null
                ? Instant.parse(tossResponse.getApprovedAt())
                : Instant.now();

        // Apply DB changes in a new transaction
        pointChargeApplier.apply(charge, tossResponse.getPaymentKey(), approvedAt);
    }
}
