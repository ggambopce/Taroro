package com.neocompany.taroro.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice // 전역 예외 처리 활성화
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리 (개발자가 명시적으로 던진 예외)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException e, HttpServletRequest req) {
        ErrorCode code = e.getErrorCode();

        // Exception에 커스텀 메시지가 있으면 사용, 없으면 기본 메시지 사용
        String message = (e.getMessage() != null && !e.getMessage().equals(code.getMessage()))
                ? e.getMessage()
                : code.getMessage();

        ErrorResponseDto body = new ErrorResponseDto(code, message);

        return ResponseEntity
                .status(code.getStatus())
                .body(body);
    }
}
