package com.neocompany.taroro.domain.taromaster.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.neocompany.taroro.domain.image.S3ImageService;
import com.neocompany.taroro.domain.masterauth.entity.MasterSettlement;
import com.neocompany.taroro.domain.masterauth.repository.MasterSettlementRepository;
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

    private static final String PROFILE_IMAGE_PREFIX = "master";

    private final TaroMasterRepository masterRepository;
    private final MasterSettlementRepository settlementRepository;
    private final S3ImageService s3ImageService;

    public Long apply(Long userId, CreateTaroMasterRequest request, MultipartFile profileImage) {
        if (masterRepository.existsByUserId(userId)) {
            throw new BusinessException(ErrorCode.MASTER_ALREADY_EXISTS);
        }

        String imageUrl = uploadProfileImageIfPresent(profileImage);

        TaroMaster master = TaroMaster.builder()
                .userId(userId)
                .displayName(request.getDisplayName())
                .intro(request.getIntro())
                .profileImageUrl(imageUrl)
                .specialties(request.getSpecialties())
                .careerYears(request.getCareerYears())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .build();

        Long masterId = masterRepository.save(master).getMasterId();

        // 정산 계좌 정보가 5개 모두 입력되어 있으면 MasterSettlement 도 함께 생성
        if (request.hasSettlementInfo()) {
            MasterSettlement settlement = MasterSettlement.builder()
                    .masterId(masterId)
                    .bankName(request.getBankName())
                    .accountNumber(request.getAccountNumber())
                    .accountHolderName(request.getAccountHolderName())
                    .phone(request.getPhone())
                    .email(request.getEmail())
                    .isVerifiedAccount(false)
                    .build();
            settlementRepository.save(settlement);
        }

        return masterId;
    }

    public Long update(Long userId, UpdateTaroMasterRequest request, MultipartFile profileImage) {
        TaroMaster master = masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));

        // 파일이 없으면 null 을 넘겨 updateProfile() 내부에서 기존 이미지 유지
        String imageUrl = uploadProfileImageIfPresent(profileImage);

        master.updateProfile(
                request.getDisplayName(),
                request.getIntro(),
                imageUrl,
                request.getSpecialties(),
                request.getCareerYears(),
                request.getIsPublic());

        return master.getMasterId();
    }

    private String uploadProfileImageIfPresent(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        return s3ImageService.uploadSingle(PROFILE_IMAGE_PREFIX, file);
    }
}
