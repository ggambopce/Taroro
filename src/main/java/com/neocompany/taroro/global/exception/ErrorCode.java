package com.neocompany.taroro.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 400 Bad Request
    INVALID_REQUEST(400, HttpStatus.BAD_REQUEST, "요청 형식이 다릅니다."),
    // 401 Unauthorized
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    // 403 Forbidden
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "권한이 없습니다."),
    // 404 Not Found
    NOT_FOUND(404, HttpStatus.NOT_FOUND, "요청을 찾을 수 없습니다."),
    // 500 Internal Server Error
    INTERNAL_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버가 혼잡 하오니 잠시후 다시 시도해주세요..."),

    // 상담방 관련 에러
    ROOM_NOT_FOUND(404, HttpStatus.NOT_FOUND, "상담방을 찾을 수 없습니다."),
    ROOM_ACCESS_DENIED(403, HttpStatus.FORBIDDEN, "상담방 접근 권한이 없습니다."),
    ROOM_INVALID_STATUS(400, HttpStatus.BAD_REQUEST, "유효하지 않은 상담방 상태입니다."),

    // 메시지 관련 에러
    MESSAGE_INVALID(400, HttpStatus.BAD_REQUEST, "유효하지 않은 메시지입니다."),

    // 페이먼츠 관련 에러
    TOSS_API_HTTP_ERROR(502, HttpStatus.BAD_GATEWAY, "결제 서버 오류."),
    TOSS_API_COMMUNICATION_ERROR(502, HttpStatus.BAD_GATEWAY, "결제 서버 통신 실패."),
    TOSS_API_RESPONSE_PARSE_ERROR(502, HttpStatus.BAD_GATEWAY, "결제 응답 파싱 실패.");

    private int code;
    private HttpStatus status;
    private String message;

    ErrorCode(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

}
