package com.neocompany.taroro.domain.room.dto.request;

import java.util.List;

import lombok.Getter;

@Getter
public class PickCardsRequest {
    private List<Integer> positions;
}
