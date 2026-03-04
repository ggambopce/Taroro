package com.neocompany.taroro.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final int code;
    private final String message;
    private final T result;

    public static <T> ApiResponse<T> ok(String message, T result) {
        return new ApiResponse<>(200, message, result);
    }

    public static <T> ApiResponse<T> failure(int code, String message, T result){
        return new ApiResponse<>(code, message, result);
    }
}
