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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HierarchicalBoardServiceImpl implements HierarchicalBoardService {

    private final PrincipalService principalService;

    private final HierarchicalBoardRepository hierarchicalBoardRepository;

    // 계층형 게시판 insert
    @Override
    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public long insertBoard(HierarchicalBoardDTO dto, Principal principal) {

        log.info("content : {}", dto.getBoardContent());

        long boardNo = insertGetBoardNo(dto, principal);

        dto = HierarchicalBoardDTO.builder()
                        .boardNo(boardNo)
                        .boardGroupNo(boardNo)
                        .boardIndent(0)
                        .boardUpperNo(String.valueOf(boardNo))
                        .build();

        insertPatchHierarchicalBoard(dto);

        return boardNo;
    }

    // 계층형 게시판 답글 insert
    @Override
    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public long insertBoardReply(HierarchicalBoardModifyDTO dto, Principal principal) {
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
                .boardUpperNo(replyDTO.getBoardUpperNo() + "," + boardNo)
                .boardNo(boardNo)
                .build();

        insertPatchHierarchicalBoard(saveDTO);

        return boardNo;

    }

    // 계층형 게시판 delete
    @Override
    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public long deleteBoard(long boardNo, Principal principal) {

        //principal을 받고 작성자와 비교 필요.

        /**
         * test
         *
         *
         * Exception 발생 시킬 시 어떻게 처리되는지.
         *
         * 수정 내역
         * 원글인지 아닌지를 indent로 비교.
         *
         * if(indent == 0)
         *  deleteGroup
         * else
         *  addDeleteDataList();
         *  deleteList();
         *
         *
         * addDeleteDataList(dataList, deleteNo, indent, deleteList){
         *      for(int i = 0; i < size(); i++){
         *          String[] arr = dataList.....split(",");
         *
         *          if(arr[indent].equals(deleteNo))
         *              deleteList.add()
         *      }
         * }
         */

        if(!principal.getName().equals(hierarchicalBoardRepository.checkWriter(boardNo)))
            new AccessDeniedException("AccessDenied");

        DeleteBoardDTO deleteData = hierarchicalBoardRepository.getDeleteData(boardNo);

//        String[] upperArr = deleteData.getBoardUpperNo().split(",");

        if(deleteData.getBoardIndent() == 0)
            hierarchicalBoardRepository.deleteByBoardGroupNo(boardNo);
        else {
            List<DeleteGroupListDTO> groupList = hierarchicalBoardRepository.getGroupList(deleteData.getBoardGroupNo());

            List<Long> deleteList = new ArrayList<>();

            //메소드 호출
            addDeleteDataList(groupList, boardNo, deleteData.getBoardIndent(), deleteList);

            hierarchicalBoardRepository.deleteAllByBoardNoList(deleteList);
        }

        log.info(boardNo + " board delete success");
        return 1L;

    }

    public void addDeleteDataList(List<DeleteGroupListDTO> groupList, long delNo, int indent, List<Long> deleteList) {

        for(int i = 0; i < groupList.size(); i++){
//            List<String> upperList = Arrays.asList(groupList.get(i).getBoardUpperNo().split(","));

            String[] upperArr = groupList.get(i).getBoardUpperNo().split(",");

            /*if(upperList.contains(String.valueOf(delNo)))
                deleteList.add(groupList.get(i).getBoardNo());*/

            if(upperArr.length > indent && upperArr[indent].equals(String.valueOf(delNo)))
                deleteList.add(groupList.get(i).getBoardNo());

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
    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public long patchBoard(HierarchicalBoardModifyDTO dto, Principal principal) {

        hierarchicalBoardRepository.boardModify(
                dto.getBoardTitle()
                , dto.getBoardContent()
                , dto.getBoardNo());

        return dto.getBoardNo();

    }

    // 1차 save 처리로 boardNo 리턴
    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public long insertGetBoardNo(HierarchicalBoardDTO dto, Principal principal){

        return hierarchicalBoardRepository.save(
                HierarchicalBoard.builder()
                        .boardTitle(dto.getBoardTitle())
                        .boardContent(dto.getBoardContent())
                        .member(principalService.checkPrincipal(principal))
                        .boardDate(Date.valueOf(LocalDate.now()))
                        .boardGroupNo(0)
                        .boardIndent(0)
                        .boardUpperNo("")
                        .build()
        ).getBoardNo();
    }


    // 계층형 게시판 insert 후 patch 처리(groupNo, UpperNo 값 설정을 위해 분리해서 처리)
    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public void insertPatchHierarchicalBoard(HierarchicalBoardDTO dto) {

        hierarchicalBoardRepository.boardInsertPatch(dto.getBoardIndent()
                , dto.getBoardGroupNo()
                , dto.getBoardUpperNo()
                , dto.getBoardNo()
        );
    }

    @Override
    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public HierarchicalBoardModifyDTO getModifyData(long boardNo, Principal principal) {

        String userId = hierarchicalBoardRepository.checkWriter(boardNo);

        if(principal == null || !principal.getName().equals(userId))
            throw new NullPointerException();

        HierarchicalBoardModifyDTO dto = hierarchicalBoardRepository.getModifyData(boardNo);

        return dto;
    }


}
