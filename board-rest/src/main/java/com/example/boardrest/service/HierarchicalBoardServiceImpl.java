package com.example.boardrest.service;

import com.example.boardrest.domain.dto.hBoard.business.DeleteBoardDTO;
import com.example.boardrest.domain.dto.hBoard.business.DeleteGroupListDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardModifyDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardReplyDTO;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardDetailDTO;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardListDTO;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardReplyInfoDTO;
import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.entity.HierarchicalBoard;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.Result;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 전체적인 체크포인트.
 *
 * Exception 처리.
 *  CustomException으로 연결 안되어있는게 너무 많으니 확인해서 수정. O
 *
 * SRP 위배 코드 체크. O
 *
 * DTO와 Query 체크.
 *
 * 반환 타입 ENUM으로 설정. SUCCESS, FAIL, ERROR 세가지로.
 *
 * ResponseEntity Mapper 생성 O
 *
 * 모두 수정 후 Front도 한번씩 체크.
 * 전체 테스트 필요.
 *
 * 서버 동작 전 테스트 코드 확인.
 */



@Service
@RequiredArgsConstructor
@Slf4j
public class HierarchicalBoardServiceImpl implements HierarchicalBoardService {

    private final PrincipalService principalService;

    private final HierarchicalBoardRepository hierarchicalBoardRepository;

    // 계층형 게시판 List
    @Override
    public Page<HierarchicalBoardListDTO> getHierarchicalBoardList(Criteria cri) {

        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
                , cri.getBoardAmount()
                , Sort.by("boardGroupNo").descending()
                        .and(Sort.by("boardUpperNo").ascending()));

        return hierarchicalBoardRepository.findAll(cri, pageable);
    }

    @Override
    public HierarchicalBoardDetailDTO getBoardDetail(long boardNo) {

        return hierarchicalBoardRepository.findBoardDetailByBoardNo(boardNo);
    }

    // 계층형 게시판 insert
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long insertBoard(HierarchicalBoardDTO dto, Principal principal) {
        HierarchicalBoardReplyDTO boardDTO = dto.toHierarchicalBoardReplyDTO();

        return insertBoardProc(boardDTO, principal);
    }

    @Override
    public HierarchicalBoardModifyDTO getModifyData(long boardNo, Principal principal) {
        HierarchicalBoard hierarchicalBoard = hierarchicalBoardRepository
                                                .findById(boardNo)
                                                .orElseThrow(() ->
                                                        new IllegalArgumentException("invalid modifyData boardNo : " + boardNo)
                                                );
        principalService.validateUser(hierarchicalBoard, principal);

        return hierarchicalBoardRepository.getModifyData(boardNo);
    }

    // 계층형 게시판 patch
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long patchBoard(HierarchicalBoardModifyDTO dto, long boardNo, Principal principal) {
        HierarchicalBoard board = hierarchicalBoardRepository
                .findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("invalid patch boardNo : " + boardNo));

        principalService.validateUser(board, principal);
        board.setPatchData(dto);
        hierarchicalBoardRepository.save(board);

        return boardNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteBoard(long boardNo, Principal principal) {
        HierarchicalBoard hierarchicalBoard = hierarchicalBoardRepository.findById(boardNo).orElseThrow(() -> new IllegalArgumentException("invalid delete boardNo : " + boardNo));
        principalService.validateUser(hierarchicalBoard, principal);

        DeleteBoardDTO deleteData = hierarchicalBoardRepository.getDeleteData(boardNo);

        if(deleteData.getBoardIndent() == 0)
            hierarchicalBoardRepository.deleteByBoardGroupNo(boardNo);
        else {
            List<DeleteGroupListDTO> groupList = hierarchicalBoardRepository.getGroupList(deleteData.getBoardGroupNo());
            List<Long> deleteList = addDeleteDataList(groupList, boardNo, deleteData.getBoardIndent());
            hierarchicalBoardRepository.deleteAllByBoardNoList(deleteList);
        }

        return Result.SUCCESS.getResultMessage();
    }

    @Override
    public HierarchicalBoardReplyInfoDTO getReplyInfo(long boardNo) {

        return hierarchicalBoardRepository.findReplyInfoByBoardNo(boardNo);
    }

    // 계층형 게시판 답글 insert
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long insertBoardReply(HierarchicalBoardReplyDTO dto, Principal principal) {

        return insertBoardProc(dto, principal);
    }


    public long insertBoardProc(HierarchicalBoardReplyDTO dto, Principal principal) {
        Member memberEntity = principalService.checkPrincipal(principal).toMemberEntity();

        HierarchicalBoard board = dto.toEntity(memberEntity);

        hierarchicalBoardRepository.save(board);
        board.setPatchDataAfterInsertion(dto);
        hierarchicalBoardRepository.save(board);

        return board.getBoardNo();
    }

    public List<Long> addDeleteDataList(List<DeleteGroupListDTO> groupList, long delNo, int indent) {

        return groupList.stream()
                .filter(v -> {
                    String[] upperArr = v.getBoardUpperNo().split(",");
                    return upperArr.length > indent && upperArr[indent].equals(String.valueOf(delNo));
                })
                .map(DeleteGroupListDTO::getBoardNo)
                .collect(Collectors.toList());
    }


}
