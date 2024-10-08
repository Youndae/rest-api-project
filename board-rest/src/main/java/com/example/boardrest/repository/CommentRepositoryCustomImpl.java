package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.comment.out.BoardCommentDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
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
    public Page<BoardCommentDTO> findAll(Pageable pageable, String boardNo, String imageNo) {

        List<BoardCommentDTO> list = jpaQueryFactory
                                    .select(
                                        Projections.fields(BoardCommentDTO.class,
                                                comment.commentNo
                                                , comment.member.nickname
                                                , comment.commentDate
                                                , new CaseBuilder()
                                                        .when(comment.commentStatus.gt(0))
                                                        .then("삭제된 댓글입니다.")
                                                        .otherwise(comment.commentContent)
                                                        .as("commentContent")
                                                , comment.commentGroupNo
                                                , comment.commentIndent
                                                , comment.commentUpperNo
                                        ))
                                    .from(comment)
                                    .innerJoin(comment.member, member)
                                    .where(commentBoardEq(boardNo, imageNo))
                                    .orderBy(comment.commentGroupNo.desc())
                                    .orderBy(comment.commentUpperNo.asc())
                                    .offset(pageable.getOffset())
                                    .limit(pageable.getPageSize())
                                    .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(comment.count())
                                                .from(comment)
                                                .where(commentBoardEq(boardNo, imageNo));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    private BooleanExpression commentBoardEq(String boardNo, String imageNo) {
        if(boardNo == null)
            return comment.imageBoard.imageNo.eq(Long.parseLong(imageNo));

        return comment.hierarchicalBoard.boardNo.eq(Long.parseLong(boardNo));
    }

}
