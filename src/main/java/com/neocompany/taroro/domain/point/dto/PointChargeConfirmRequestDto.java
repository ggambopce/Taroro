package com.neocompany.taroro.domain.point.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PointChargeConfirmRequestDto {

    @NotBlank
    private String paymentKey;

    @NotBlank
    private String orderId;
}
