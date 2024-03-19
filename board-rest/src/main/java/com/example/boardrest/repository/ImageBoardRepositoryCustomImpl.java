package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.ImageBoardDTO;
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

@Repository
@RequiredArgsConstructor
public class ImageBoardRepositoryCustomImpl implements ImageBoardRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ImageBoardDTO> findAll(Criteria cri, Pageable pageable) {

        List<ImageBoardDTO> list = jpaQueryFactory
                .select(
                        Projections.fields(
                                ImageBoardDTO.class
                                , imageBoard.imageNo
                                , imageBoard.imageTitle
                                , imageBoard.member.userId
                                , imageBoard.imageDate
                                , imageBoard.imageContent
                                , imageData.imageName
                                )
                )
                .from(imageBoard)
                .innerJoin(imageData)
                .on(imageBoard.imageNo.eq(imageData.imageBoard.imageNo))
                .where(searchTypeEq(cri.getSearchType(), cri.getKeyword()))
                .groupBy(imageBoard.imageNo)
                .groupBy(imageData.imageName)
                .orderBy(imageBoard.imageNo.desc())
                .offset((cri.getPageNum() - 1) * cri.getImageAmount())
                .limit(cri.getImageAmount())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(imageBoard.countDistinct())
                                            .from(imageBoard)
                                            .where(searchTypeEq(cri.getSearchType(), cri.getKeyword()));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    private BooleanExpression searchTypeEq(String searchType, String keyword) {
        if(searchType == null)
            return null;
        else if(searchType.equals("t"))
            return imageBoard.imageTitle.like(keyword);
        else if(searchType.equals("c"))
            return imageBoard.imageContent.like(keyword);
        else if(searchType.equals("tc"))
            return imageBoard.imageTitle.like(keyword).or(imageBoard.imageContent.like(keyword));
        else if(searchType.equals("u"))
            return imageBoard.member.userId.like(keyword);
        else
            return null;
    }
}
