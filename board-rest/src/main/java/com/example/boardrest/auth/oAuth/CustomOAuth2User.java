package com.example.boardrest.auth.oAuth;

import com.example.boardrest.auth.user.CustomUserDetails;
import com.example.boardrest.auth.oAuth.domain.OAuth2DTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomOAuth2User implements OAuth2User, CustomUserDetails {

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

        return oAuth2DTO.getAuthList()
                .stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getAuth()))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return oAuth2DTO.getUsername();
    }

    @Override
    public String getUserId() {
        return oAuth2DTO.getUserId();
    }

    public String getNickname() {
        return oAuth2DTO.getNickname();
    }

}
