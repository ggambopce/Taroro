package com.neocompany.taroro.global.oauth2;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.neocompany.taroro.domain.users.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;
    private Map<String, Object> attributes;

    // 일반 로그인 사용자
    public PrincipalDetails(User user) {
        this.user = user;
        this.attributes = null;
    }

    // 소셜 로그인 사용자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // === OAuth2User ===
    @Override
    public Map<String, Object> getAttributes() { return attributes; }

    @Override
    public String getName() {
        return String.valueOf(user.getUserId());
    }

    // === UserDetails ===
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_USER,ROLE_ADMIN" → [SimpleGrantedAuthority("ROLE_USER"), ...]
        return Arrays.stream(user.getRoles().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableList());
    }


    @Override
    public String getPassword() { return user.getPasswordHash(); }

    @Override
    public String getUsername() {
        // 인증의 기준 키를 하나로 통일: 이메일을 권장
        return user.getEmail();
    }

    public String getEmail() {
        return user.getEmail();
    }
    public String getUserName() {return user.getName();}

    /**
     * ROLE_ 접두어 포함 여부와 무관하게 검사 가능
     * hasRole("ADMIN") / hasRole("ROLE_ADMIN") 둘 다 허용
     */
    public boolean hasRole(String role) {
        String target = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(target));
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isUser() {
        return hasRole("USER");
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}