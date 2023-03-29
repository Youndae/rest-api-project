package com.example.boardrest.service;

import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.entity.HierarchicalBoard;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.HierarchicalBoardModifyDTO;
import com.example.boardrest.domain.dto.HierarchicalBoardReplyDTO;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    public long insertBoard(HierarchicalBoardDTO dto, Principal principal) {
        try {
            log.info("content : {}", dto.getBoardContent());

            long boardNo = insertGetBoardNo(dto, principal);

            dto = HierarchicalBoardDTO.builder()
                            .boardNo(boardNo)
                            .boardGroupNo(boardNo)
                            .boardIndent(0)
                            .boardUpperNo(String.valueOf(boardNo))
                            .build();

            /*request.setAttribute("boardNo", boardNo);
            request.setAttribute("boardGroupNo", boardNo);
            request.setAttribute("boardIndent", 0);
            request.setAttribute("boardUpperNo", boardNo);*/

            /*return insertPatchHierarchicalBoard(request);*/
            insertPatchHierarchicalBoard(dto);

            return boardNo;
        } catch (Exception e) {
            log.info("Board insertion failed");
            return -1;
        }
    }

    // 계층형 게시판 답글 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long insertBoardReply(HierarchicalBoardModifyDTO dto, Principal principal) {
        try {
            /**
             * 1. boardNo를 통해 해당 게시글 data 가져오기
             * @Data
             * 1. groupNo
             * 2. upperNo
             * 3. indent
             *
             * 2. 해당 데이터를 파싱해서 HierarchicalBoardDTO에 담고 insert처리.
             * 3. insert 처리시 getBoardNo();
             */

            HierarchicalBoardDTO saveDTO = HierarchicalBoardDTO.builder()
                    .boardTitle(dto.getBoardTitle())
                    .boardContent(dto.getBoardContent())
                    .build();

            long boardNo = insertGetBoardNo(saveDTO, principal);

            HierarchicalBoardReplyDTO replyDTO = hierarchicalBoardRepository.getReplyData(dto.getBoardNo());

            saveDTO = HierarchicalBoardDTO.builder()
                    .boardIndent(replyDTO.getBoardIndent() + 1)
                    .boardGroupNo(replyDTO.getBoardGroupNo())
                    .boardUpperNo(replyDTO.getBoardUpperNo() + String.valueOf(boardNo))
                    .boardNo(boardNo)
                    .build();

            insertPatchHierarchicalBoard(saveDTO);

            return boardNo;
        } catch (Exception e) {
            log.info("board Reply insertion failed");
            return -1;
        }
    }

    // 계층형 게시판 delete
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteBoard(long boardNo) {
        try {
            hierarchicalBoardRepository.deleteById(boardNo);
            log.info(boardNo + " board delete success");
        } catch (Exception e) {
            log.info(boardNo + " delete failed");

        }
    }

    // 계층형 게시판 List
    @Override
    public Page<HierarchicalBoardDTO> getHierarchicalBoardList(Criteria cri) {

        log.info("getHierarchicalBoard list");

        Page<HierarchicalBoardDTO> dto;

        log.info("service pageNum : " + cri.getPageNum());
        log.info("service keyword : " + cri.getKeyword());
        log.info("service searchType : " + cri.getSearchType());

        if(cri.getKeyword() != null)
            cri.setKeyword("%"+cri.getKeyword()+"%");

        if (cri.getKeyword() == null) { //default List
            log.info("default");
            dto = hierarchicalBoardRepository.hierarchicalBoardList(
                    PageRequest.of(cri.getPageNum() - 1
                            , cri.getBoardAmount()
                            , Sort.by("boardGroupNo").descending()
                                    .and(Sort.by("boardUpperNo").ascending()))
            );
        } else if (cri.getSearchType().equals("t")) {//title 검색시 사용
            log.info("searchType is t");
            dto = hierarchicalBoardRepository.hierarchicalBoardListSearchTitle(
                    cri.getKeyword()
                    , PageRequest.of(cri.getPageNum() - 1
                            , cri.getBoardAmount()
                            , Sort.by("boardGroupNo").descending()
                                    .and(Sort.by("boardUpperNo").ascending()))
            );
        } else if (cri.getSearchType().equals("c")) {//content 검색시 사용
            log.info("searchType is c");
            dto = hierarchicalBoardRepository.hierarchicalBoardListSearchContent(
                    cri.getKeyword()
                    , PageRequest.of(cri.getPageNum() - 1
                            , cri.getBoardAmount()
                            , Sort.by("boardGroupNo").descending()
                                    .and(Sort.by("boardUpperNo").ascending()))
            );
        } else if (cri.getSearchType().equals("u")) {// user 검색 시 사용
            log.info("searchType is u");
            dto = hierarchicalBoardRepository.hierarchicalBoardListSearchUser(
                    cri.getKeyword()
                    , PageRequest.of(cri.getPageNum() - 1
                            , cri.getBoardAmount()
                            , Sort.by("boardGroupNo").descending()
                                    .and(Sort.by("boardUpperNo").ascending()))
            );
        } else if (cri.getSearchType().equals("tc")) {// title and content 검색시 사용
            log.info("searchType is tc");
            dto = hierarchicalBoardRepository.hierarchicalBoardListSearchTitleOrContent(
                    cri.getKeyword()
                    , PageRequest.of(cri.getPageNum() - 1
                            , cri.getBoardAmount()
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
    @Transactional(rollbackOn = Exception.class)
    public long patchBoard(HierarchicalBoardModifyDTO dto, Principal principal) {

            hierarchicalBoardRepository.boardModify(
                    dto.getBoardTitle()
                    , dto.getBoardContent()
                    , dto.getBoardNo());

            return dto.getBoardNo();

    }

    // 1차 save 처리로 boardNo 리턴
    public long insertGetBoardNo(HierarchicalBoardDTO dto, Principal principal) throws Exception {

        return hierarchicalBoardRepository.save(
                HierarchicalBoard.builder()
                        .boardTitle(dto.getBoardTitle())
                        .boardContent(dto.getBoardContent())
                        .member(principalService.checkPrincipal(principal))
                        .boardDate(Date.valueOf(LocalDate.now()))
                        .build()
        ).getBoardNo();
    }


    // 계층형 게시판 insert 후 patch 처리(groupNo, UpperNo 값 설정을 위해 분리해서 처리)
    public void insertPatchHierarchicalBoard(HierarchicalBoardDTO dto) throws Exception {

        hierarchicalBoardRepository.boardInsertPatch(dto.getBoardIndent()
                , dto.getBoardGroupNo()
                , dto.getBoardUpperNo()
                , dto.getBoardNo()
        );
    }

    @Override
    public HierarchicalBoardModifyDTO getModifyData(long boardNo, Principal principal) {

        String userId = hierarchicalBoardRepository.checkWriter(boardNo);

        if(principal == null || !principal.getName().equals(userId))
            return null;

        HierarchicalBoardModifyDTO dto = hierarchicalBoardRepository.getModifyData(boardNo);

        return dto;
    }


}
