package com.neocompany.taroro.domain.tarocard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.tarocard.dto.request.CreateTaroCardRequest;
import com.neocompany.taroro.domain.tarocard.dto.request.UpdateTaroCardRequest;
import com.neocompany.taroro.domain.tarocard.entity.ArcanaType;
import com.neocompany.taroro.domain.tarocard.entity.SuitType;
import com.neocompany.taroro.domain.tarocard.entity.TaroCard;
import com.neocompany.taroro.domain.tarocard.repository.TaroCardRepository;
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
public class TaroCardCommandService {

    private final TaroCardRepository cardRepository;
    private final TaroCardSetRepository cardSetRepository;
    private final TaroMasterRepository masterRepository;

    public Long create(Long userId, CreateTaroCardRequest request) {
        TaroMaster master = requireMaster(userId);

        TaroCardSet set = cardSetRepository.findBySetIdAndDeletedFalse(request.getSetId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CARD_SET_NOT_FOUND));

        if (!set.isOwnedBy(master.getMasterId())) {
            throw new BusinessException(ErrorCode.CARD_SET_ACCESS_DENIED);
        }

        TaroCard card = TaroCard.builder()
                .setId(set.getSetId())
                .masterId(master.getMasterId())
                .cardName(request.getCardName())
                .cardNumber(request.getCardNumber())
                .arcanaType(ArcanaType.valueOf(request.getArcanaType()))
                .suit(request.getSuit() != null ? SuitType.valueOf(request.getSuit()) : null)
                .keywords(request.getKeywords())
                .cardDescription(request.getCardDescription())
                .uprightMeaning(request.getUprightMeaning())
                .reversedMeaning(request.getReversedMeaning())
                .imageUrl(request.getImageUrl())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return cardRepository.save(card).getCardId();
    }

    public Long update(Long userId, Long cardId, UpdateTaroCardRequest request) {
        TaroMaster master = requireMaster(userId);
        TaroCard card = getOwnedCard(cardId, master.getMasterId());
        card.update(request.getCardName(), request.getKeywords(),
                request.getCardDescription(), request.getUprightMeaning(),
                request.getReversedMeaning(), request.getImageUrl(), request.getIsActive());
        return card.getCardId();
    }

    public void delete(Long userId, Long cardId) {
        TaroMaster master = requireMaster(userId);
        TaroCard card = getOwnedCard(cardId, master.getMasterId());
        card.softDelete();
    }

    private TaroMaster requireMaster(Long userId) {
        return masterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
    }

    private TaroCard getOwnedCard(Long cardId, Long masterId) {
        TaroCard card = cardRepository.findByCardIdAndDeletedFalse(cardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARD_NOT_FOUND));
        if (!card.isOwnedBy(masterId)) {
            throw new BusinessException(ErrorCode.CARD_ACCESS_DENIED);
        }
        return card;
    }
}
