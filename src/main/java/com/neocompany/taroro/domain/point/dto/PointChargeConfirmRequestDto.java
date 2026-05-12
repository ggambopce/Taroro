package com.neocompany.taroro.domain.point.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PointChargeConfirmRequestDto {

    @NotBlank(message = "결제 키가 누락되었습니다.")
    private String paymentKey;

    @NotBlank(message = "주문 ID가 누락되었습니다.")
    private String orderId;
}
