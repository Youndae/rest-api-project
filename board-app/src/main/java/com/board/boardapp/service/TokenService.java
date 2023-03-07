package com.board.boardapp.service;

import com.board.boardapp.dto.JwtDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface TokenService {

    //토큰 존재 여부 체크
    String checkExistsToken(HttpServletRequest request);

    void saveToken(JwtDTO jwtDTO, HttpServletResponse response);

    JwtDTO reIssuedToken(HttpServletRequest request, HttpServletResponse response);

}
