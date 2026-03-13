package com.neocompany.taroro.global.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompExceptionHandler extends StompSubProtocolErrorHandler
        implements SmartInitializingSingleton {

    private final ObjectMapper objectMapper;

    @Autowired
    private SubProtocolWebSocketHandler subProtocolWebSocketHandler;

    /**
     * 모든 싱글톤 빈 생성 완료 후 자기 자신을 STOMP 에러 핸들러로 등록
     */
    @Override
    public void afterSingletonsInstantiated() {
        subProtocolWebSocketHandler.getProtocolHandlerMap().values().stream()
                .filter(StompSubProtocolHandler.class::isInstance)
                .map(StompSubProtocolHandler.class::cast)
                .forEach(h -> h.setErrorHandler(this));
        log.info("[StompExceptionHandler] STOMP error handler registered");
    }

    /**
     * 클라이언트 메시지 처리 중 발생한 예외를 ERROR 프레임으로 반환
     */
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        String errorMessage = cause.getMessage() != null ? cause.getMessage() : "서버 오류";
        log.warn("[STOMP] Error: {}", errorMessage);

        try {
            byte[] body = objectMapper.writeValueAsBytes(new WebSocketErrorPayload(403, errorMessage));

            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
            accessor.setMessage(errorMessage);
            accessor.setLeaveMutable(true);

            return MessageBuilder.createMessage(body, accessor.getMessageHeaders());
        } catch (JsonProcessingException e) {
            return super.handleClientMessageProcessingError(clientMessage, ex);
        }
    }
}
