package com.neocompany.taroro.domain.master.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 마스터 상태 변경 요청
 * status: AVAILABLE | BUSY | OFFLINE
 */
@Getter
@NoArgsConstructor
public class MasterStatusRequest {
    private String status;
}
