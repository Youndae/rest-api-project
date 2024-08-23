package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardListDTO;
import com.example.boardrest.domain.entity.QMember;
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

import static com.example.boardrest.domain.entity.QHierarchicalBoard.hierarchicalBoard;
import static com.example.boardrest.domain.entity.QMember.member;


@Repository
@RequiredArgsConstructor
public class HierarchicalBoardRepositoryCustomImpl implements HierarchicalBoardRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<HierarchicalBoardListDTO> findAll(Criteria cri, Pageable pageable) {

        List<HierarchicalBoardListDTO> list = jpaQueryFactory.select(
                        Projections.fields(
                                HierarchicalBoardListDTO.class
                                , hierarchicalBoard.boardNo
                                , hierarchicalBoard.boardTitle
                                , hierarchicalBoard.member.nickname
                                , hierarchicalBoard.boardDate
                                , hierarchicalBoard.boardIndent
                        )
                )
                .from(hierarchicalBoard)
                .innerJoin(hierarchicalBoard.member, member)
                .where(
                        searchTypeEq(cri.getSearchType(), cri.getKeyword())
                )
                                                .orderBy(hierarchicalBoard.boardGroupNo.desc())
                                                .orderBy(hierarchicalBoard.boardUpperNo.asc())
                                                .offset(pageable.getOffset())
                                                .limit(pageable.getPageSize())
                                                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(hierarchicalBoard.countDistinct())
                                            .from(hierarchicalBoard)
                                            .where(
                                                    searchTypeEq(cri.getSearchType(), cri.getKeyword())
                                            );

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    private BooleanExpression searchTypeEq(String searchType, String keyword) {
        if(searchType == null)
            return null;
        else if(searchType.equals("t"))
            return hierarchicalBoard.boardTitle.like(keyword);
        else if(searchType.equals("c"))
            return hierarchicalBoard.boardContent.like(keyword);
        else if(searchType.equals("tc"))
            return hierarchicalBoard.boardTitle.like(keyword).or(hierarchicalBoard.boardContent.like(keyword));
        else if(searchType.equals("u"))
            return hierarchicalBoard.member.userId.like(keyword);
        else
            return null;
    }
}
