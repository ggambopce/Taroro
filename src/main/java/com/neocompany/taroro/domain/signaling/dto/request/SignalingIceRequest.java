package com.neocompany.taroro.domain.signaling.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignalingIceRequest {
    private Long targetUserId;
    private Object candidate;
}
