package com.example.boardrest.service;

import com.example.boardrest.domain.dto.member.in.JoinRequest;
import com.example.boardrest.domain.dto.member.in.OAuthJoinRequest;
import com.example.boardrest.domain.dto.member.in.UpdateProfileRequest;
import com.example.boardrest.domain.dto.member.out.ProfileResponse;
import com.example.boardrest.domain.enums.MemberCheckResult;

import java.security.Principal;

public interface MemberService {

//    String memberLogin(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response);

    String register(JoinRequest joinRequest);

    void oAuthJoin(OAuthJoinRequest request, String userId);

    MemberCheckResult checkId(String userId);

    MemberCheckResult checkNickname(String nickname, Principal principal);

//    String logout(HttpServletRequest request, HttpServletResponse response, Principal principal);

    void updateProfile(UpdateProfileRequest updateProfileRequest, Principal principal);

    ProfileResponse getProfile(Principal principal);

}
