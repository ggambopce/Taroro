package com.neocompany.taroro.domain.message.dto.request;

import com.neocompany.taroro.domain.message.entity.MessageType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendMessageRequest {

    private String content;
    private MessageType messageType = MessageType.TEXT;
}
