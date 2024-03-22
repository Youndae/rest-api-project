package com.example.boardrest.service;

import com.example.boardrest.domain.dto.MemberDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.dto.JwtDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

public interface MemberService {

    int memberJoinProc(MemberDTO dto);

    Long memberLogin(Member member, HttpServletRequest request, HttpServletResponse response);

    int logout(HttpServletRequest request, HttpServletResponse response, Principal principal);
}
