package com.neocompany.taroro.global.oauth2;

import java.util.Map;

public class NaverUserInfo implements Oauth2UserInfo {
    private Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    @Override
    public String getId() {
        Object responseObj = attributes.get("response");

        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) responseObj;
        Object id = response.get("id");

        return String.valueOf(id);
    }

    @Override
    public String getEmail() {
        Object responseObj = attributes.get("response");
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) responseObj;
        Object email = response.get("email");

        return String.valueOf(email);
    }

    @Override
    public String getName() {
        Object responseObj = attributes.get("response");
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) responseObj;
        Object name = response.get("name");

        return String.valueOf(name);
    }
}
