package com.neocompany.taroro.domain.tarocardset.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.tarocardset.dto.request.CreateTaroCardSetRequest;
import com.neocompany.taroro.domain.tarocardset.dto.request.UpdateTaroCardSetRequest;
import com.neocompany.taroro.domain.tarocardset.entity.TaroCardSet;
import com.neocompany.taroro.domain.tarocardset.repository.TaroCardSetRepository;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TaroCardSetCommandService {

    private final TaroCardSetRepository cardSetRepository;
    private final TaroMasterRepository masterRepository;

    public Long create(Long userId, CreateTaroCardSetRequest request) {
        TaroMaster master = requireMaster(userId);

        TaroCardSet set = TaroCardSet.builder()
                .masterId(master.getMasterId())
                .setName(request.getSetName())
                .setDescription(request.getSetDescription())
                .brandName(request.getBrandName())
                .publisherName(request.getPublisherName())
                .coverImageUrl(request.getCoverImageUrl())
                .cardCount(request.getCardCount())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return cardSetRepository.save(set).getSetId();
    }

    public Long update(Long userId, Long setId, UpdateTaroCardSetRequest request) {
        TaroMaster master = requireMaster(userId);
        TaroCardSet set = getOwnedSet(setId, master.getMasterId());
        set.update(request.getSetName(), request.getSetDescription(),
                request.getCoverImageUrl(), request.getIsPublic(), request.getIsActive());
        return set.getSetId();
    }

    public void delete(Long userId, Long setId) {
        TaroMaster master = requireMaster(userId);
        TaroCardSet set = getOwnedSet(setId, master.getMasterId());
        set.softDelete();
    }

    private TaroMaster requireMaster(Long userId) {
        return masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
    }

    private TaroCardSet getOwnedSet(Long setId, Long masterId) {
        TaroCardSet set = cardSetRepository.findBySetIdAndDeletedFalse(setId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARD_SET_NOT_FOUND));
        if (!set.isOwnedBy(masterId)) {
            throw new BusinessException(ErrorCode.CARD_SET_ACCESS_DENIED);
        }
        return set;
    }
}
