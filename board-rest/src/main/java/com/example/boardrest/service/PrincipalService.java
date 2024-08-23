package com.example.boardrest.service;

import com.example.boardrest.domain.dto.auth.PrincipalDTO;

import java.security.Principal;

public interface PrincipalService {

    PrincipalDTO checkPrincipal(Principal principal);

    String getNicknameToPrincipal(Principal principal);

    void validateUser(Object entity, Principal principal);
}
