package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.common.business.PageCondition;
import com.example.boardrest.domain.dto.imageBoard.business.ImageBoardDetail;
import com.example.boardrest.domain.dto.imageBoard.business.ImageBoardPatchDetail;
import com.example.boardrest.domain.dto.imageBoard.out.ImageBoardListResponse;
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

import static com.example.boardrest.domain.entity.QImageBoard.imageBoard;
import static com.example.boardrest.domain.entity.QImageData.imageData;
import static com.example.boardrest.domain.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class ImageBoardRepositoryCustomImpl implements ImageBoardRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ImageBoardListResponse> findAllListByPageable(PageCondition condition, Pageable pageable) {

        List<ImageBoardListResponse> list = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ImageBoardListResponse.class,
                                imageBoard.id,
                                imageBoard.title,
                                imageData.imageName.min()
                        )
                )
                .from(imageBoard)
                .innerJoin(imageData)
                .on(imageBoard.id.eq(imageData.imageBoard.id))
                .where(searchTypeEq(condition.getSearchType(), condition.getKeyword()))
                .groupBy(imageBoard.id)
                .orderBy(imageBoard.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(imageBoard.countDistinct())
                                            .from(imageBoard)
                                            .where(searchTypeEq(condition.getSearchType(), condition.getKeyword()));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    private BooleanExpression searchTypeEq(String searchType, String keyword) {
        if(searchType == null)
            return null;
        else if(searchType.equals("t"))
            return imageBoard.title.like(keyword);
        else if(searchType.equals("c"))
            return imageBoard.content.like(keyword);
        else if(searchType.equals("tc"))
            return imageBoard.title.like(keyword).or(imageBoard.content.like(keyword));
        else if(searchType.equals("u"))
            return imageBoard.member.userId.like(keyword);
        else
            return null;
    }

    @Override
    public ImageBoardDetail findDetailById(long id) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                ImageBoardDetail.class,
                                imageBoard.title,
                                imageBoard.content,
                                imageBoard.member.nickname.as("writer"),
                                imageBoard.member.userId.as("writerId"),
                                imageBoard.createdAt
                        )
                )
                .from(imageBoard)
                .innerJoin(imageBoard.member, member)
                .fetchOne();
    }

    @Override
    public ImageBoardPatchDetail findPatchDetailById(long id) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                ImageBoardPatchDetail.class,
                                imageBoard.member.userId.as("writer"),
                                imageBoard.title,
                                imageBoard.content
                        )
                )
                .from(imageBoard)
                .innerJoin(imageBoard.member, member)
                .fetchOne();
    }
}
