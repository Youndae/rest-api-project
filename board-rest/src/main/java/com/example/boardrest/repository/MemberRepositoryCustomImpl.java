package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.member.out.ProfileResponse;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enums.OAuthProvider;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import static com.example.boardrest.domain.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ProfileResponse getMemberProfileDataByUserId(String userId) {

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                ProfileResponse.class,
                                member.nickname,
                                member.email,
                                member.profile
                        )
                )
                .from(member)
                .where(member.userId.eq(userId))
                .fetchOne();
    }

    @Override
    public Member findOAuthUserByUserId(String userId) {
        return jpaQueryFactory
                .selectFrom(member)
                .where(
                        member.userId.eq(userId)
                                .and(
                                        member.provider.ne(OAuthProvider.LOCAL.getKey())
                                )
                )
                .fetchOne();
    }
}
