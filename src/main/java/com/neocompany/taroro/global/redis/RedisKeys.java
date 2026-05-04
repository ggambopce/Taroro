package com.neocompany.taroro.global.redis;

public final class RedisKeys {

    private RedisKeys() {}

    public static String userOnline(Long userId) {
        return "user:online:" + userId;
    }

    public static String stompSession(String sessionId) {
        return "stomp:session:" + sessionId;
    }

    public static String unread(Long userId, Long roomId) {
        return "unread:" + userId + ":" + roomId;
    }

    public static String roomRecent(Long roomId) {
        return "room:recent:" + roomId;
    }
}
