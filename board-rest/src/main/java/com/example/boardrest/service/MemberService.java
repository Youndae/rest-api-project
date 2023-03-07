package com.example.boardrest.service;

import com.example.boardrest.domain.Member;
import com.example.boardrest.domain.dto.JwtDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MemberService {

    int memberJoinProc(Member member);

    JwtDTO memberLogin(Member member);
}
