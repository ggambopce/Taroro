package com.neocompany.taroro.domain.taromaster.entity;

import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

public enum MasterStatus {
    ONLINE, BUSY, BREAK, OFFLINE;

    public static MasterStatus from(String value) {
        try {
            return MasterStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "유효하지 않은 마스터 상태입니다: " + value);
        }
    }
}
