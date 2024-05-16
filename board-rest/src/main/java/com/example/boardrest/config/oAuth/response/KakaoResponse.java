package com.example.boardrest.config.oAuth.response;

import com.example.boardrest.domain.enumuration.OAuthProvider;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    private final Map<String, Object> account_attribute;

    private final Map<String, Object> profile_attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.account_attribute = (Map<String, Object>) attribute.get("kakao_account");
        this.profile_attribute = (Map<String, Object>) account_attribute.get("profile");
    }

    @Override
    public String getProvider() {
        return OAuthProvider.KAKAO.getKey();
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return account_attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return profile_attribute.get("nickname").toString();
    }

}
