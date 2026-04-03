package com.example.boardrest.service;

import com.example.boardrest.domain.entity.Member;

public interface PrincipalService {

    void validateUser(String writer, String userId);

    Member getMemberByUserId(String userId);
}
