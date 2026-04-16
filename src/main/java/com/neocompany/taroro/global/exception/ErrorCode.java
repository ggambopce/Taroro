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
    TOSS_API_RESPONSE_PARSE_ERROR(502, HttpStatus.BAD_GATEWAY, "결제 응답 파싱 실패."),

    // 타로 마스터 관련 에러
    MASTER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "마스터를 찾을 수 없습니다."),
    MASTER_ALREADY_EXISTS(400, HttpStatus.BAD_REQUEST, "이미 마스터 신청이 존재합니다."),
    MASTER_NOT_APPROVED(403, HttpStatus.FORBIDDEN, "승인된 마스터만 접근 가능합니다."),
    MASTER_ACCESS_DENIED(403, HttpStatus.FORBIDDEN, "마스터 접근 권한이 없습니다."),

    // 카드 세트 관련 에러
    CARD_SET_NOT_FOUND(404, HttpStatus.NOT_FOUND, "카드 세트를 찾을 수 없습니다."),
    CARD_SET_ACCESS_DENIED(403, HttpStatus.FORBIDDEN, "카드 세트 접근 권한이 없습니다."),

    // 타로 카드 관련 에러
    CARD_NOT_FOUND(404, HttpStatus.NOT_FOUND, "카드를 찾을 수 없습니다."),
    CARD_ACCESS_DENIED(403, HttpStatus.FORBIDDEN, "카드 접근 권한이 없습니다."),

    // 마스터 플랜 관련 에러
    PLAN_NOT_FOUND(404, HttpStatus.NOT_FOUND, "플랜을 찾을 수 없습니다."),
    PLAN_ACCESS_DENIED(403, HttpStatus.FORBIDDEN, "플랜 접근 권한이 없습니다."),

    // 마스터 정산/인증 관련 에러
    SETTLEMENT_NOT_FOUND(404, HttpStatus.NOT_FOUND, "정산 정보를 찾을 수 없습니다."),
    SETTLEMENT_ALREADY_EXISTS(400, HttpStatus.BAD_REQUEST, "이미 정산 정보가 존재합니다."),
    VERIFICATION_NOT_FOUND(404, HttpStatus.NOT_FOUND, "인증 정보를 찾을 수 없습니다."),
    PASS_VERIFICATION_FAILED(400, HttpStatus.BAD_REQUEST, "PASS 인증에 실패했습니다.");

    private int code;
    private HttpStatus status;
    private String message;

    ErrorCode(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

}
