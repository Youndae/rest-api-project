package com.example.boardrest.service;

import com.example.boardrest.auth.user.CustomUser;
import com.example.boardrest.domain.dto.member.out.MemberStatusResponse;
import com.example.boardrest.domain.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@Slf4j
public class AuthContextService {

    public MemberStatusResponse getMemberStatus(Authentication authentication) {
        String userId;

        if(authentication.getPrincipal() instanceof CustomUser){
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            userId = customUser.getUserId();
        } else {
            userId = (String) authentication.getPrincipal();
        }

        String role = authentication.getAuthorities().stream()
                .map(auth -> Role.of(auth.getAuthority()))
                .max(Comparator.comparing(Role::ordinal))
                .map(Role::getKey)
                .orElse(Role.MEMBER.getKey());

        return MemberStatusResponse.builder()
                .userId(userId)
                .role(role)
                .build();
    }
}
