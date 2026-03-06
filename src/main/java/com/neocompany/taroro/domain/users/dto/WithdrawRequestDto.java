package com.neocompany.taroro.domain.users.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawRequestDto {
    // normal 계정만 사용, social은 비워서 보낸다.
    // @Pattern은 null 허용 — 소셜 로그인은 password 없이 요청
    @Size(min = 8, max = 64)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]{8,}$",
             message = "비밀번호는 영문자와 숫자를 각 1개 이상 포함하고 8자 이상이어야 합니다.")
    private String password;
}
