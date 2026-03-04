package com.neocompany.taroro.domain.users.dto;

import java.time.Instant;

import com.neocompany.taroro.domain.users.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeAuthResponseDto {
    private String email;
    private String loginType;
    private String userName;
    private String userRole;
    private Instant createdAt;      // 가입일

    public static MeAuthResponseDto of(User user) {

        return MeAuthResponseDto.builder()
                .email(user.getEmail())
                .loginType(user.getLoginType())
                .userName(user.getName())
                .userRole(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
