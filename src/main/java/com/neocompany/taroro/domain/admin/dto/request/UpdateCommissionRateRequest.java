package com.neocompany.taroro.domain.admin.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateCommissionRateRequest {

    @NotNull(message = "수수료율을 입력해주세요.")
    @Min(value = 0, message = "수수료율은 0 이상이어야 합니다.")
    @Max(value = 100, message = "수수료율은 100 이하여야 합니다.")
    private Integer rate;
}
