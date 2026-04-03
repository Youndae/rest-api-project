package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.comment.out.BoardCommentResponse;
import com.example.boardrest.domain.entity.Comment;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.boardrest.domain.entity.QComment.comment;
import static com.example.boardrest.domain.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<BoardCommentResponse> findAllCommentByBoardId(long id, Pageable pageable) {
        BooleanExpression boardCondition = comment.board.id.eq(id);

        return findAllCommentList(pageable, boardCondition);
    }

    @Override
    public Page<BoardCommentResponse> findAllCommentByImageBoardId(long id, Pageable pageable) {
        BooleanExpression boardCondition = comment.imageBoard.id.eq(id);

        return findAllCommentList(pageable, boardCondition);
    }

    private Page<BoardCommentResponse> findAllCommentList(Pageable pageable, BooleanExpression condition) {

        List<BoardCommentResponse> list = jpaQueryFactory
                .select(
                        Projections.constructor(
                                BoardCommentResponse.class,
                                comment.id,
                                comment.member.nickname,
                                comment.member.userId,
                                comment.createdAt,
                                comment.content,
                                comment.indent,
                                comment.deletedAt
                        ))
                .from(comment)
                .innerJoin(comment.member, member)
                .where(condition)
                .orderBy(comment.groupNo.desc())
                .orderBy(comment.upperNo.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(comment.count())
                .from(comment)
                .where(condition);

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    @Override
    public Comment findNotDeleteCommentById(long targetId) {
        return jpaQueryFactory
                .selectFrom(comment)
                .where(comment.id.eq(targetId).and(comment.deletedAt.isNull()))
                .fetchOne();
    }
}
