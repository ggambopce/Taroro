package com.neocompany.taroro.global.oauth2;

import java.util.Map;

public class KakaoUserInfo implements Oauth2UserInfo {
    // 카카오 API에서 반환하는 json 데이터를 저장하는 맵
    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Object accountObj = attributes.get("kakao_account");
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) accountObj;
        Object email = kakaoAccount.get("email");

        return String.valueOf(email);
    }

    @Override
    public String getName() {
        Object kakaoAccountObj = attributes.get("kakao_account");
        if (kakaoAccountObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;

            Object profileObj = kakaoAccount.get("profile");
            if (profileObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> profile = (Map<String, Object>) profileObj;
                Object nickname = profile.get("nickname");
                if (nickname != null) {
                    return String.valueOf(nickname);
                }
            }
            return "카카오사용자";
        }
        return "카카오사용자";
    }
}
