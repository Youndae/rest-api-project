package com.example.boardrest.service;

import com.example.boardrest.domain.dto.MemberDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.dto.JwtDTO;

public interface MemberService {

    int memberJoinProc(MemberDTO dto);

    JwtDTO memberLogin(Member member);
}
