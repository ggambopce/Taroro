package com.neocompany.taroro.domain.withdrawal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RejectWithdrawalRequest {

    @NotBlank(message = "거절 사유를 입력해주세요.")
    private String reason;
}
