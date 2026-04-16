package com.neocompany.taroro.domain.taromaster.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.taromaster.dto.request.CreateTaroMasterRequest;
import com.neocompany.taroro.domain.taromaster.dto.request.UpdateTaroMasterRequest;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TaroMasterCommandService {

    private final TaroMasterRepository masterRepository;

    public Long apply(Long userId, CreateTaroMasterRequest request) {
        if (masterRepository.existsByUserId(userId)) {
            throw new BusinessException(ErrorCode.MASTER_ALREADY_EXISTS);
        }

        TaroMaster master = TaroMaster.builder()
                .userId(userId)
                .displayName(request.getDisplayName())
                .intro(request.getIntro())
                .profileImageUrl(request.getProfileImageUrl())
                .specialties(request.getSpecialties())
                .careerYears(request.getCareerYears())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .build();

        return masterRepository.save(master).getMasterId();
    }

    public Long update(Long userId, UpdateTaroMasterRequest request) {
        TaroMaster master = masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));

        master.updateProfile(
                request.getDisplayName(),
                request.getIntro(),
                request.getProfileImageUrl(),
                request.getSpecialties(),
                request.getCareerYears(),
                request.getIsPublic());

        return master.getMasterId();
    }
}
