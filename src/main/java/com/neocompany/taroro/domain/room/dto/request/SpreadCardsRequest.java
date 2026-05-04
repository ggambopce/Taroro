package com.neocompany.taroro.domain.room.dto.request;

import java.util.List;

import lombok.Getter;

@Getter
public class SpreadCardsRequest {
    private Long setId;
    private List<Long> cardIds;
}
