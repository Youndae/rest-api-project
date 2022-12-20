package com.example.boardrest.service;

import com.example.boardrest.domain.Member;

import java.security.Principal;

public interface PrincipalService {

    Member checkPrincipal(Principal principal);
}
