package com.neocompany.taroro.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.neocompany.taroro.global.response.GlobalApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스/유효성 에러 → HTTP 200, body statusCode 201
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        String message = (e.getMessage() != null && !e.getMessage().equals(code.getMessage()))
                ? e.getMessage()
                : code.getMessage();
        return ResponseEntity.ok()
                .body(GlobalApiResponse.failure(201, message));
    }

    // 잘못된 경로 → HTTP 404, body statusCode 404
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<GlobalApiResponse<Void>> handleNotFound() {
        return ResponseEntity.status(404)
                .body(GlobalApiResponse.failure(404, "존재하지 않는 엔드포인트 입니다."));
    }

    // 서버 에러 → HTTP 500, body statusCode 502
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.status(500)
                .body(GlobalApiResponse.failure(502, "서버가 혼잡 하오니 잠시후 다시 시도해주세요..."));
    }
}
