package com.neocompany.taroro.domain.message.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.neocompany.taroro.global.redis.RedisKeys;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnreadCountService {

    private final StringRedisTemplate redis;

    public void increment(Long userId, Long roomId) {
        redis.opsForValue().increment(RedisKeys.unread(userId, roomId));
    }

    public void reset(Long userId, Long roomId) {
        redis.opsForValue().set(RedisKeys.unread(userId, roomId), "0");
    }

    /** -1 = 캐시 미스 → 호출자에서 DB fallback */
    public Map<Long, Long> getBatch(Long userId, List<Long> roomIds) {
        if (roomIds.isEmpty()) return Map.of();
        List<String> keys = roomIds.stream().map(id -> RedisKeys.unread(userId, id)).toList();
        List<String> vals = redis.opsForValue().multiGet(keys);
        Map<Long, Long> result = new HashMap<>();
        for (int i = 0; i < roomIds.size(); i++) {
            String v = vals != null ? vals.get(i) : null;
            result.put(roomIds.get(i), v != null ? Long.parseLong(v) : -1L);
        }
        return result;
    }
}
