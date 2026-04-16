package com.neocompany.taroro.domain.masterauth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.masterauth.dto.request.PassVerificationRequest;
import com.neocompany.taroro.domain.masterauth.dto.response.VerificationResponse;
import com.neocompany.taroro.domain.masterauth.entity.MasterVerification;
import com.neocompany.taroro.domain.masterauth.repository.MasterVerificationRepository;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MasterVerificationService {

    private final MasterVerificationRepository verificationRepository;
    private final TaroMasterRepository masterRepository;

    @Transactional(readOnly = true)
    public VerificationResponse getMy(Long userId) {
        TaroMaster master = requireMaster(userId);
        MasterVerification verification = verificationRepository.findByMasterId(master.getMasterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_NOT_FOUND));
        return new VerificationResponse(verification);
    }

    public void processPassVerification(Long userId, PassVerificationRequest request) {
        TaroMaster master = requireMaster(userId);

        MasterVerification verification = verificationRepository.findByMasterId(master.getMasterId())
                .orElseGet(() -> verificationRepository.save(
                        MasterVerification.builder().masterId(master.getMasterId()).build()));

        if (Boolean.TRUE.equals(request.getVerified())) {
            verification.completePassVerification();
        } else {
            verification.failPassVerification("PASS 인증 실패");
            throw new BusinessException(ErrorCode.PASS_VERIFICATION_FAILED);
        }
    }

    private TaroMaster requireMaster(Long userId) {
        return masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
    }
}
