package com.neocompany.taroro.global.dto;

import java.util.List;

/**
 * 목록 조회 응답 공통 래퍼
 * 사용: GlobalApiResponse<PageResult<XxxResponse>>
 */
public record PageResult<T>(
        List<T> items,
        int limit,
        int offset,
        boolean hasNext
) {
    public static <T> PageResult<T> of(List<T> items, int limit, int offset) {
        boolean hasNext = items.size() == limit;
        return new PageResult<>(items, limit, offset, hasNext);
    }
}
