package com.example.boardrest.service;

import com.example.boardrest.domain.dto.MemberDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.dto.JwtDTO;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

public interface MemberService {

    int memberJoinProc(MemberDTO dto);

    JwtDTO memberLogin(Member member, HttpServletRequest request);

    int logout(HttpServletRequest request, Principal principal);
}
