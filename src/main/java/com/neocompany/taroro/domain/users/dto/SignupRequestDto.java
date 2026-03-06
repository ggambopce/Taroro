package com.neocompany.taroro.domain.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank
    @Size(max=40)
    @Email
    private String email;

    /**
     * 사용자 이름 규칙
     * 한글 또는 영문만 허용
     * 공백 불가,숫자 및 모든 특수문자 불가
     * 길이 2 ~ 20자
     */
    @NotBlank
    @Size(min=2, max=20)
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s._-]{2,40}$")
    private String name;

    /**
     * 비밀번호 정규식
     * 영문자(대문자 또는 소문자)와 숫자가 최소 1개 이상씩 포함
     * 특수문자 !@#$%^&*()_+-={}[]:;"'<>,.?/ 허용
     * 최소 8자 이상
     */
    @NotBlank
    @Size(min = 8, max = 64)
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]{8,}$")
    private String password;

    @NotBlank
    private String confirmPassword;
}
