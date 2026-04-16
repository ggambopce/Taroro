package com.neocompany.taroro.domain.masterauth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PassVerificationRequest {
    private String txId;
    private Boolean verified;
    private String ci;
    private String phone;
    private String userName;
}
