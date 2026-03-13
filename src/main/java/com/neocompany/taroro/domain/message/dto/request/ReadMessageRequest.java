package com.neocompany.taroro.domain.message.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadMessageRequest {

    private Long lastReadMessageId;
}

