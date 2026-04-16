package com.neocompany.taroro.domain.masterplan.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMasterPlanRequest {
    private String planName;
    private String planDescription;
    private Integer counselingMinutes;
    private Long price;
    private Integer discountRate;
    private Boolean isActive;
    private Boolean isPublic;
}
