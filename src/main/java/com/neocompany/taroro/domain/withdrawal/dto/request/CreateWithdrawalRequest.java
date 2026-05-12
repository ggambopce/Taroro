package com.neocompany.taroro.domain.withdrawal.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateWithdrawalRequest {

    @NotNull(message = "출금 금액을 입력해주세요.")
    @Min(value = 1, message = "출금 금액은 1원 이상이어야 합니다.")
    private Long amount;
}
