package com.board.boardapp.service;


import javax.servlet.http.HttpServletRequest;


public interface TokenService {

    //토큰 존재 여부 체크
    //글작성 페이지 접근 판단을 위해 토큰 존재여부만 체크
    boolean checkExistsToken(HttpServletRequest request);

}
