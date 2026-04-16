package com.neocompany.taroro.domain.masterauth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateSettlementRequest {
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String phone;
    private String email;
}
