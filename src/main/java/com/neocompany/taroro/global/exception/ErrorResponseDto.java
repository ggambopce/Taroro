package com.neocompany.taroro.global.exception;

import lombok.Getter;

@Getter
public class ErrorResponseDto {
    private final int code;          // 숫자 코드
    private final String status;     // HttpStatus 이름 (예: "BAD_REQUEST")
    private final String message;    // 예외 메시지


    public ErrorResponseDto(ErrorCode errorCode, String message) {
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus().toString();
        this.message = message;
    }
}
