package com.neocompany.taroro.domain.message.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neocompany.taroro.domain.message.dto.response.ChatMessageResponse;
import com.neocompany.taroro.global.redis.RedisKeys;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageCacheService {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    private static final int MAX_CACHED = 50;
    private static final Duration TTL = Duration.ofHours(24);

    public void push(Long roomId, ChatMessageResponse msg) {
        try {
            String key = RedisKeys.roomRecent(roomId);
            redis.opsForList().leftPush(key, objectMapper.writeValueAsString(msg));
            redis.opsForList().trim(key, 0, MAX_CACHED - 1);
            redis.expire(key, TTL);
        } catch (JsonProcessingException e) {
            log.warn("[MessageCache] 직렬화 실패 roomId={}: {}", roomId, e.getMessage());
        }
    }

    /** null 반환 = 캐시 미스 → DB fallback */
    public List<ChatMessageResponse> getRecent(Long roomId, int size) {
        List<String> raw = redis.opsForList().range(RedisKeys.roomRecent(roomId), 0, size - 1);
        if (raw == null || raw.isEmpty()) return null;
        return raw.stream().map(this::deserialize).toList();
    }

    public void evict(Long roomId) {
        redis.delete(RedisKeys.roomRecent(roomId));
    }

    private ChatMessageResponse deserialize(String json) {
        try {
            return objectMapper.readValue(json, ChatMessageResponse.class);
        } catch (JsonProcessingException e) {
            log.warn("[MessageCache] 역직렬화 실패: {}", e.getMessage());
            return null;
        }
    }
}
