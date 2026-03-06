package com.neocompany.taroro.domain.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointChargeReadyResponseDto {
    private Long chargeId;
    private String orderId;
    private long amount;
}
