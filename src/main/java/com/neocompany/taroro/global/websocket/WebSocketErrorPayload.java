package com.neocompany.taroro.global.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebSocketErrorPayload {
    private final int code;
    private final String message;
}
