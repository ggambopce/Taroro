package com.neocompany.taroro.domain.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PointChargeReadyRequestDto {

    @NotNull
    @Min(1)
    private Long pointAmount;
}
