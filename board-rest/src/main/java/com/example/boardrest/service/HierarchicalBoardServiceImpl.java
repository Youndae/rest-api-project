package com.example.boardrest.service;

import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.entity.HierarchicalBoard;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

        return insertBoardProc(dto, principal);
    }

    // 계층형 게시판 답글 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long insertBoardReply(HierarchicalBoardModifyDTO dto, Principal principal) {
        HierarchicalBoardReplyDTO replyDTO = hierarchicalBoardRepository.getReplyData(dto.getBoardNo());

        HierarchicalBoardDTO boardDTO = HierarchicalBoardDTO.builder()
                                                            .boardTitle(dto.getBoardTitle())
                                                            .boardContent(dto.getBoardContent())
                                                            .boardGroupNo(replyDTO.getBoardGroupNo())
                                                            .boardIndent(replyDTO.getBoardIndent() + 1)
                                                            .boardUpperNo(replyDTO.getBoardUpperNo())
                                                            .build();

        return insertBoardProc(boardDTO, principal);
    }


    public long insertBoardProc(HierarchicalBoardDTO dto, Principal principal) {

        HierarchicalBoard board = HierarchicalBoard.builder()
                                                    .boardTitle(dto.getBoardTitle())
                                                    .boardContent(dto.getBoardContent())
                                                    .member(principalService.checkPrincipal(principal))
                                                    .boardDate(Date.valueOf(LocalDate.now()))
                                                    .build();

        hierarchicalBoardRepository.save(board);
        board.setPatchBoardData(dto);
        hierarchicalBoardRepository.save(board);

        return board.getBoardNo();
    }

    // 계층형 게시판 delete
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long deleteBoard(long boardNo, Principal principal) {
        //principal을 받고 작성자와 비교 필요.
        if(!principal.getName().equals(hierarchicalBoardRepository.checkWriter(boardNo)))
            new AccessDeniedException("AccessDenied");

        DeleteBoardDTO deleteData = hierarchicalBoardRepository.getDeleteData(boardNo);

        if(deleteData.getBoardIndent() == 0)
            hierarchicalBoardRepository.deleteByBoardGroupNo(boardNo);
        else {
            List<DeleteGroupListDTO> groupList = hierarchicalBoardRepository.getGroupList(deleteData.getBoardGroupNo());

            List<Long> deleteList = addDeleteDataList(groupList, boardNo, deleteData.getBoardIndent());

            hierarchicalBoardRepository.deleteAllByBoardNoList(deleteList);
        }

        log.info(boardNo + " board delete success");
        return 1L;
    }

    public List<Long> addDeleteDataList(List<DeleteGroupListDTO> groupList, long delNo, int indent) {
        List<Long> deleteList = new ArrayList<>();

        for(int i = 0; i < groupList.size(); i++){
            String[] upperArr = groupList.get(i).getBoardUpperNo().split(",");

            if(upperArr.length > indent && upperArr[indent].equals(String.valueOf(delNo)))
                deleteList.add(groupList.get(i).getBoardNo());
        }

        return deleteList;
    }

    // 계층형 게시판 List
    @Override
    public Page<HierarchicalBoardDTO> getHierarchicalBoardList(Criteria cri) {

        Page<HierarchicalBoardDTO> dto;

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
            throw new IllegalArgumentException("IllegalArg");
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

    @Override
    @Transactional(rollbackOn = Exception.class)
    public HierarchicalBoardModifyDTO getModifyData(long boardNo, Principal principal) {

        String userId = hierarchicalBoardRepository.checkWriter(boardNo);

        if(principal == null || !principal.getName().equals(userId))
            throw new NullPointerException();

        HierarchicalBoardModifyDTO dto = hierarchicalBoardRepository.getModifyData(boardNo);

        return dto;
    }


}
