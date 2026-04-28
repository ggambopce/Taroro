package com.neocompany.taroro.domain.tarocardset.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.tarocardset.dto.response.TaroCardSetResponse;
import com.neocompany.taroro.domain.tarocardset.entity.TaroCardSet;
import com.neocompany.taroro.domain.tarocardset.repository.TaroCardSetRepository;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaroCardSetQueryService {

    private final TaroCardSetRepository cardSetRepository;
    private final TaroMasterRepository masterRepository;

    public PageResult<TaroCardSetResponse> getPublicSets(
            String keyword, Long masterId, Boolean isActive, int limit, int offset) {
        Slice<TaroCardSet> slice = cardSetRepository.findPublicSets(
                keyword, masterId, isActive, PageRequest.of(offset / limit, limit));
        return new PageResult<>(
                slice.getContent().stream().map(TaroCardSetResponse::new).toList(),
                limit, offset, slice.hasNext());
    }

    public TaroCardSetResponse getSet(Long setId, Long requesterId) {
        TaroCardSet set = cardSetRepository.findBySetIdAndDeletedFalse(setId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARD_SET_NOT_FOUND));

        if (!set.isPublic()) {
            // 비공개: 본인 마스터만 조회 가능
            TaroMaster master = masterRepository.findByUserId(requesterId).orElse(null);
            if (master == null || !set.isOwnedBy(master.getMasterId())) {
                throw new BusinessException(ErrorCode.CARD_SET_ACCESS_DENIED);
            }
        }
        return new TaroCardSetResponse(set);
    }

    public PageResult<TaroCardSetResponse> getMySets(Long userId, int limit, int offset) {
        TaroMaster master = masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
        Slice<TaroCardSet> slice = cardSetRepository.findAllByMasterIdAndDeletedFalse(
                master.getMasterId(), PageRequest.of(offset / limit, limit));
        return new PageResult<>(
                slice.getContent().stream().map(TaroCardSetResponse::new).toList(),
                limit, offset, slice.hasNext());
    }

    public TaroCardSetResponse getSetForAdmin(Long setId) {
        TaroCardSet set = cardSetRepository.findBySetIdAndDeletedFalse(setId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARD_SET_NOT_FOUND));
        return new TaroCardSetResponse(set);
    }
}
