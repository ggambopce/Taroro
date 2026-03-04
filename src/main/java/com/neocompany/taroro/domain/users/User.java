package com.neocompany.taroro.domain.users;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(length = 20)
    private String age;

    @Column(length = 20)
    private String birth;

    @Column(length = 20)
    private String nickname;

    @Column(length = 20)
    private String gender;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private boolean is_auth = false;

    @Column(nullable = false)
    private boolean is_taro_master = false;

    @Column(nullable = false, length = 20)
    private String loginType;       // normal, kakao, google, apple

    @Column(nullable = false)
    private String roles = "ROLE_USER"; // ROLE_USER, ROLE_ADMIN

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreationTimestamp
    private Instant createdAt;
}
