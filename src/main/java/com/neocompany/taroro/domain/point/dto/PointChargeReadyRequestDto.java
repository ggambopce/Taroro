package com.neocompany.taroro.domain.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PointChargeReadyRequestDto {

    @NotNull(message = "충전 금액을 입력해주세요.")
    @Min(value = 1, message = "충전 금액은 1 이상이어야 합니다.")
    private Long pointAmount;
}
