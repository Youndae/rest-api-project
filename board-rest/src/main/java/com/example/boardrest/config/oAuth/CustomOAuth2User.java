package com.example.boardrest.config.oAuth;

import com.example.boardrest.domain.dto.OAuth2DTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2DTO oAuth2DTO;

    public CustomOAuth2User(OAuth2DTO oAuth2DTO) {
        this.oAuth2DTO = oAuth2DTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        oAuth2DTO.getAuthList().forEach(v -> System.out.println("OAuth2User auth : " + v));

        oAuth2DTO.getAuthList().forEach(v -> collection.add((GrantedAuthority) v::getAuth));

        return collection;
    }

    @Override
    public String getName() {
        return oAuth2DTO.getUsername();
    }

    public String getUserId() {
        return oAuth2DTO.getUserId();
    }

    public String getNickname() {
        return oAuth2DTO.getNickname();
    }

}
