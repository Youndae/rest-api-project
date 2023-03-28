package com.example.boardrest.service;

import com.example.boardrest.domain.dto.JwtDTO;

import javax.servlet.http.HttpServletRequest;

public interface TokenService {

    public JwtDTO reIssuedToken(HttpServletRequest request);
}
