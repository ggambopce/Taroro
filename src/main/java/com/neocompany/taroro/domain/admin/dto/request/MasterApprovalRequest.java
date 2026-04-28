package com.neocompany.taroro.domain.admin.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MasterApprovalRequest {
    private String approvalStatus; // APPROVED | REJECTED
    private String reason;
}
