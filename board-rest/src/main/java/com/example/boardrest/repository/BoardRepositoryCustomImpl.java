package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.board.business.BoardPatchDetail;
import com.example.boardrest.domain.dto.board.out.BoardDetailResponse;
import com.example.boardrest.domain.dto.common.business.PageCondition;
import com.example.boardrest.domain.dto.board.out.BoardListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.boardrest.domain.entity.QBoard.board;
import static com.example.boardrest.domain.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<BoardListResponse> findAllListByPageable(PageCondition condition, Pageable pageable) {

        List<BoardListResponse> list = jpaQueryFactory.select(
                        Projections.constructor(
                                BoardListResponse.class,
                                board.id,
                                board.title,
                                board.member.nickname.as("writer"),
                                board.createdAt,
                                board.indent
                        )
                )
                .from(board)
                .innerJoin(board.member, member)
                .where(
                        searchTypeEq(condition.getSearchType(), condition.getKeyword())
                )
                .orderBy(board.groupNo.desc())
                .orderBy(board.upperNo.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(board.countDistinct())
                                            .from(board)
                                            .where(
                                                    searchTypeEq(condition.getSearchType(), condition.getKeyword())
                                            );

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    private BooleanExpression searchTypeEq(String searchType, String keyword) {
        if(searchType == null)
            return null;
        else if(searchType.equals("t"))
            return board.title.like(keyword);
        else if(searchType.equals("c"))
            return board.content.like(keyword);
        else if(searchType.equals("tc"))
            return board.title.like(keyword).or(board.content.like(keyword));
        else if(searchType.equals("u"))
            return board.member.userId.like(keyword);
        else
            return null;
    }

    @Override
    public BoardDetailResponse findDetailResponseById(long id) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                BoardDetailResponse.class,
                                board.title,
                                board.member.nickname.as("writer"),
                                board.member.userId.as("writerId"),
                                board.content,
                                board.createdAt
                        )
                )
                .from(board)
                .innerJoin(board.member, member)
                .where(board.id.eq(id))
                .fetchOne();
    }

    @Override
    public BoardPatchDetail findPatchDetailById(long id) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                BoardPatchDetail.class,
                                board.member.userId,
                                board.title,
                                board.content
                        )
                )
                .from(board)
                .innerJoin(board.member, member)
                .fetchOne();
    }

    @Override
    @Transactional
    public void deleteByPath(long groupNo, String selfUpper, String childUpper) {
        jpaQueryFactory
                .delete(board)
                .where(
                        board.groupNo.eq(groupNo)
                                .and(
                                        board.upperNo.eq(selfUpper)
                                                .or(
                                                        board.upperNo.like(childUpper)
                                                )

                                )
                )
                .execute();
    }
}
