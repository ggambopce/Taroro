package com.neocompany.taroro.domain.room.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.neocompany.taroro.global.redis.RedisKeys;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OnlineStatusService {

    private final StringRedisTemplate redis;
    private static final Duration TTL = Duration.ofMinutes(30);

    public void setOnline(Long userId, String stompSessionId) {
        redis.opsForValue().set(RedisKeys.userOnline(userId), "1", TTL);
        redis.opsForValue().set(RedisKeys.stompSession(stompSessionId), String.valueOf(userId), TTL);
    }

    public void setOffline(Long userId) {
        redis.delete(RedisKeys.userOnline(userId));
    }

    public Map<Long, Boolean> isOnlineBatch(List<Long> userIds) {
        if (userIds.isEmpty()) return Map.of();
        List<String> keys = userIds.stream().map(RedisKeys::userOnline).toList();
        List<String> vals = redis.opsForValue().multiGet(keys);
        Map<Long, Boolean> result = new HashMap<>();
        for (int i = 0; i < userIds.size(); i++) {
            result.put(userIds.get(i), vals != null && vals.get(i) != null);
        }
        return result;
    }
}
