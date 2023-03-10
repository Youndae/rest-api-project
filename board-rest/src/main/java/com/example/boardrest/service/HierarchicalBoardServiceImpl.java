package com.example.boardrest.service;

import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.HierarchicalBoard;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class HierarchicalBoardServiceImpl implements HierarchicalBoardService {

    private final PrincipalService principalService;

    private final HierarchicalBoardRepository hierarchicalBoardRepository;

    // 계층형 게시판 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long insertBoard(HttpServletRequest request, Principal principal) {
        try {

            long boardNo = insertGetBoardNo(request, principal);

            request.setAttribute("boardNo", boardNo);
            request.setAttribute("boardGroupNo", boardNo);
            request.setAttribute("boardIndent", 0);
            request.setAttribute("boardUpperNo", boardNo);

            return insertPatchHierarchicalBoard(request);
        } catch (Exception e) {
            log.info("Board insertion failed");
            return -1;
        }
    }

    // 계층형 게시판 답글 insert
    @Override
    public long insertBoardReply(HttpServletRequest request, Principal principal) {
        try {
            long boardNo = insertGetBoardNo(request, principal);

            request.setAttribute("boardNo", boardNo);
            request.setAttribute("boardGroupNo", request.getParameter("boardGroupNo"));
            request.setAttribute("boardIndent", request.getParameter("boardIndent") + 1);
            request.setAttribute("boardUpperNo", request.getParameter("boardUpperNo") + "," + boardNo);

            return insertPatchHierarchicalBoard(request);
        } catch (Exception e) {
            log.info("board Reply insertion failed");
            return -1;
        }
    }

    // 계층형 게시판 delete
    @Override
    public long deleteBoard(long boardNo) {
        try {
            hierarchicalBoardRepository.deleteById(boardNo);
            log.info(boardNo + " board delete success");
            return 1;
        } catch (Exception e) {
            log.info(boardNo + " delete failed");
            return -1;
        }
    }

    // 계층형 게시판 List
    @Override
    public Page<HierarchicalBoardDTO> getHierarchicalBoardList(Criteria cri) {

        Page<HierarchicalBoardDTO> dto;

        log.info("service pageNum : " + cri.getPageNum());
        log.info("service keyword : " + cri.getKeyword());
        log.info("service searchType : " + cri.getSearchType());

        if(cri.getKeyword() != null) cri.setKeyword("%"+cri.getKeyword()+"%");

        if (cri.getKeyword() == null) { //default List
            log.info("default");
            dto = hierarchicalBoardRepository.hierarchicalBoardList(
                    PageRequest.of(cri.getPageNum() - 1
                            , cri.getAmount()
                            , Sort.by("boardGroupNo").descending()
                                    .and(Sort.by("boardUpperNo").ascending()))
            );
        } else if (cri.getSearchType().equals("t")) {//title 검색시 사용
            log.info("searchType is t");
            dto = hierarchicalBoardRepository.hierarchicalBoardListSearchTitle(
                    cri.getKeyword()
                    , PageRequest.of(cri.getPageNum() - 1
                            , cri.getAmount()
                            , Sort.by("boardGroupNo").descending()
                                    .and(Sort.by("boardUpperNo").ascending()))
            );
        } else if (cri.getSearchType().equals("c")) {//content 검색시 사용
            log.info("searchType is c");
            dto = hierarchicalBoardRepository.hierarchicalBoardListSearchContent(
                    cri.getKeyword()
                    , PageRequest.of(cri.getPageNum() - 1
                            , cri.getAmount()
                            , Sort.by("boardGroupNo").descending()
                                    .and(Sort.by("boardUpperNo").ascending()))
            );
        } else if (cri.getSearchType().equals("u")) {// user 검색 시 사용
            log.info("searchType is u");
            dto = hierarchicalBoardRepository.hierarchicalBoardListSearchUser(
                    cri.getKeyword()
                    , PageRequest.of(cri.getPageNum() - 1
                            , cri.getAmount()
                            , Sort.by("boardGroupNo").descending()
                                    .and(Sort.by("boardUpperNo").ascending()))
            );
        } else if (cri.getSearchType().equals("tc")) {// title and content 검색시 사용
            log.info("searchType is tc");
            dto = hierarchicalBoardRepository.hierarchicalBoardListSearchTitleOrContent(
                    cri.getKeyword()
                    , PageRequest.of(cri.getPageNum() - 1
                            , cri.getAmount()
                            , Sort.by("boardGroupNo").descending()
                                    .and(Sort.by("boardUpperNo").ascending()))
            );
        } else {
            log.info("error");
            return null;
        }

        log.info("ok");
        log.info("response : " + dto);

        return dto;

    }

    // 계층형 게시판 patch
    @Override
    public long patchBoard(HttpServletRequest request) {
        return hierarchicalBoardRepository.boardModify(
                request.getParameter("boardTitle")
                , request.getParameter("boardContent")
                , Long.parseLong(request.getParameter("boardNo"))
        ).getBoardNo();
    }

    // 1차 save 처리로 boardNo 리턴
    public long insertGetBoardNo(HttpServletRequest request, Principal principal) throws Exception {
        return hierarchicalBoardRepository.save(
                HierarchicalBoard.builder()
                        .boardTitle(request.getParameter("boardTitle"))
                        .member(principalService.checkPrincipal(principal))
                        .boardDate(Date.valueOf(LocalDate.now()))
                        .build()
        ).getBoardNo();
    }


    // 계층형 게시판 insert 후 patch 처리(groupNo, UpperNo 값 설정을 위해 분리해서 처리)
    public long insertPatchHierarchicalBoard(HttpServletRequest request) throws Exception {

        return hierarchicalBoardRepository.boardInsertPatch(request.getParameter("boardContent")
                , Integer.parseInt(request.getAttribute("boardIndent").toString())
                , Long.parseLong(request.getAttribute("boardGroupNo").toString())
                , request.getAttribute("boardUpperNo").toString()
                , Long.parseLong(request.getAttribute("boardNo").toString())
        ).getBoardNo();
    }
}
