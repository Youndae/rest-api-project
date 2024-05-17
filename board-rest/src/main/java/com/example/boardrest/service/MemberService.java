package com.example.boardrest.service;

import com.example.boardrest.domain.dto.JoinDTO;
import com.example.boardrest.domain.dto.ProfileDTO;
import com.example.boardrest.domain.dto.ProfileResponseDTO;
import com.example.boardrest.domain.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

public interface MemberService {

    int memberJoinProc(JoinDTO dto, MultipartFile profileThumbnail);

    Long memberLogin(Member member, HttpServletRequest request, HttpServletResponse response);

    int logout(HttpServletRequest request, HttpServletResponse response, Principal principal);

    Long modifyProfile(ProfileDTO profileDTO, Principal principal);

    ProfileResponseDTO getProfile(Principal principal);

}
