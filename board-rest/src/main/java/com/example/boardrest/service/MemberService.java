package com.example.boardrest.service;

import com.example.boardrest.domain.dto.member.in.JoinDTO;
import com.example.boardrest.domain.dto.member.in.ProfileDTO;
import com.example.boardrest.domain.dto.member.out.ProfileResponseDTO;
import com.example.boardrest.domain.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

public interface MemberService {

    String memberJoinProc(JoinDTO dto, MultipartFile profileThumbnail);

    String checkId(String userId);

    String checkNickname(String nickname, Principal principal);

    String memberLogin(Member member, HttpServletRequest request, HttpServletResponse response);

    String logout(HttpServletRequest request, HttpServletResponse response, Principal principal);

    String modifyProfile(ProfileDTO profileDTO, Principal principal);

    ProfileResponseDTO getProfile(Principal principal);

}
