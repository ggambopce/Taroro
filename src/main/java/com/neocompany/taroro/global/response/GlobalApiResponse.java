package com.neocompany.taroro.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalApiResponse<T> {
    private final boolean success;
    private final String message;
    private final int statusCode;
    private final T data;

    public static <T> GlobalApiResponse<T> ok(String message, T data) {
        return new GlobalApiResponse<>(true, message, 200, data);
    }

    public static <T> GlobalApiResponse<T> failure(int statusCode, String message) {
        return new GlobalApiResponse<>(false, message, statusCode, null);
    }
}
