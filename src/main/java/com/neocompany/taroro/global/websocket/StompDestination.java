package com.neocompany.taroro.global.websocket;

/**
 * STOMP destination 상수 모음
 *
 * broadcast  → /topic/...
 * personal   → /queue/...  (SimpMessagingTemplate.convertAndSendToUser 에서 /user 접두사 자동 추가)
 */
public final class StompDestination {

    private StompDestination() {}

    // ── 방 브로드캐스트 ────────────────────────────────────────────────────────
    /** /topic/rooms/{roomId} — 방 이벤트 (메시지, 읽음, 타이핑, 상태 변경) */
    public static String room(Long roomId) {
        return "/topic/rooms/" + roomId;
    }

    /** /topic/waiting-room — 대기열 이벤트 */
    public static final String WAITING_ROOM = "/topic/waiting-room";

    /** /topic/masters/status — 마스터 상태 변경 브로드캐스트 */
    public static final String MASTERS_STATUS = "/topic/masters/status";

    // ── 개인 큐 (convertAndSendToUser 경유, /user/{id}/queue/... 로 전달) ──────
    /** /queue/events — 개인 알림 (매칭 완료, 상담 초대 등) */
    public static final String USER_NOTIFICATIONS = "/queue/events";

    /** /queue/signaling — WebRTC 시그널링 (offer/answer/ice) */
    public static final String USER_SIGNALING = "/queue/signaling";

    /** /queue/errors — 개인 에러 알림 */
    public static final String USER_ERRORS = "/queue/errors";
}
