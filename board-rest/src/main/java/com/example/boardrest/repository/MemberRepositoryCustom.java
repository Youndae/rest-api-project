package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.member.out.ProfileResponse;
import com.example.boardrest.domain.entity.Member;

public interface MemberRepositoryCustom {

    ProfileResponse getMemberProfileDataByUserId(String userId);

    Member findOAuthUserByUserId(String userId);
}
