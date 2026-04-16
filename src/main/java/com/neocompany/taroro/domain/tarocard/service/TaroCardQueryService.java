package com.neocompany.taroro.domain.tarocard.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.tarocard.dto.response.TaroCardResponse;
import com.neocompany.taroro.domain.tarocard.dto.response.TaroCardSummaryResponse;
import com.neocompany.taroro.domain.tarocard.entity.TaroCard;
import com.neocompany.taroro.domain.tarocard.repository.TaroCardRepository;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaroCardQueryService {

    private final TaroCardRepository cardRepository;

    public PageResult<TaroCardSummaryResponse> getCardsBySet(Long setId, String keyword,
                                                              Boolean isActive, int limit, int offset) {
        Slice<TaroCard> slice = cardRepository.findBySet(
                setId, keyword, isActive,
                PageRequest.of(offset / limit, limit));

        return PageResult.of(
                slice.getContent().stream().map(TaroCardSummaryResponse::new).toList(),
                limit, offset);
    }

    public TaroCardResponse getCard(Long cardId) {
        TaroCard card = cardRepository.findByCardIdAndDeletedFalse(cardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARD_NOT_FOUND));
        return new TaroCardResponse(card);
    }
}
