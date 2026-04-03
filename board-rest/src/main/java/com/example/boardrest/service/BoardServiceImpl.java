package com.example.boardrest.service;

import com.example.boardrest.customException.CustomNotFoundException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.board.business.BoardPatchDetail;
import com.example.boardrest.domain.dto.board.in.BoardReplyRequest;
import com.example.boardrest.domain.dto.board.in.BoardRequest;
import com.example.boardrest.domain.dto.board.out.BoardDetailResponse;
import com.example.boardrest.domain.dto.board.out.BoardListResponse;
import com.example.boardrest.domain.dto.board.out.BoardPatchDetailResponse;
import com.example.boardrest.domain.dto.common.business.PageCondition;
import com.example.boardrest.domain.dto.common.in.ListRequest;
import com.example.boardrest.domain.dto.response.PageResponse;
import com.example.boardrest.domain.entity.Board;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.ListAmount;
import com.example.boardrest.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final PrincipalService principalService;

    private final BoardRepository boardRepository;

    // 계층형 게시판 List
    @Override
    public PageResponse<BoardListResponse> getBoardList(ListRequest request) {
        PageCondition condition = PageCondition.of(request, ListAmount.BOARD);

        Pageable pageable = PageRequest.of(condition.getPage() - 1,
                condition.getAmount(),
                Sort.by("groupNo").descending()
                        .and(Sort.by("upperNo").ascending()));

        Page<BoardListResponse> content = boardRepository.findAllListByPageable(condition, pageable);

        return PageResponse.of(content);
    }

    @Override
    public BoardDetailResponse getBoardDetail(long id) {

        BoardDetailResponse content = boardRepository.findDetailResponseById(id);

        if(content == null){
            log.warn("Board detail content is null. board id: {}", id);
            throw new CustomNotFoundException(ErrorCode.BAD_REQUEST, ErrorCode.BAD_REQUEST.getMessage());
        }

        return content;
    }

    // 계층형 게시판 insert
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long insertBoard(BoardRequest dto, String userId) {
        Member memberEntity = principalService.getMemberByUserId(userId);

        Board boardEntity = dto.toEntity(memberEntity);

        boardRepository.save(boardEntity);
        boardEntity.initializeRootPath();

        return boardEntity.getId();
    }

    @Override
    public BoardPatchDetailResponse getPatchData(long id, String userId) {
        BoardPatchDetail patchDetail = boardRepository.findPatchDetailById(id);

        principalService.validateUser(patchDetail.getUserId(), userId);

        return BoardPatchDetailResponse.builder()
                .title(patchDetail.getTitle())
                .content(patchDetail.getContent())
                .build();
    }

    // 계층형 게시판 patch
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long patchBoard(BoardRequest request, long id, String userId) {
        Board board = boardRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("invalid patch boardNo : " + id));

        principalService.validateUser(board.getMember().getUserId(), userId);
        board.setPatchData(request);

        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBoard(long id, String userId) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("invalid delete boardNo : " + id));
        principalService.validateUser(board.getMember().getUserId(), userId);

        if(board.getIndent() == 0)
            boardRepository.deleteByBoardGroupNo(id);
        else {
            String selfUpperNo = board.getUpperNo();
            String childUpperNo = board.getUpperNo() + ",%";

            boardRepository.deleteByPath(board.getGroupNo(), selfUpperNo, childUpperNo);
        }
    }

    @Override
    public void getReplyInfo(long id) {
        boardRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.BAD_REQUEST, "reply target board is null"));
    }

    // 계층형 게시판 답글 insert
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long insertBoardReply(long targetId, BoardReplyRequest request, String userId) {
        Board targetBoard = boardRepository.findById(targetId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.BAD_REQUEST, "reply target board is null"));

        Member memberEntity = principalService.getMemberByUserId(userId);

        Board board = request.toEntity(memberEntity, targetBoard);
        boardRepository.save(board);
        board.initializeReplyPath(targetBoard.getUpperNo());

        return board.getId();
    }
}
