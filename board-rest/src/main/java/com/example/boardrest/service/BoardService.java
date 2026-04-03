package com.example.boardrest.service;

import com.example.boardrest.domain.dto.board.in.BoardReplyRequest;
import com.example.boardrest.domain.dto.board.out.BoardDetailResponse;
import com.example.boardrest.domain.dto.board.out.BoardListResponse;
import com.example.boardrest.domain.dto.board.out.BoardPatchDetailResponse;
import com.example.boardrest.domain.dto.board.in.BoardRequest;
import com.example.boardrest.domain.dto.common.in.ListRequest;
import com.example.boardrest.domain.dto.response.PageResponse;

public interface BoardService {

    PageResponse<BoardListResponse> getBoardList(ListRequest request);

    BoardDetailResponse getBoardDetail(long id);

    long insertBoard(BoardRequest dto, String userId);

    BoardPatchDetailResponse getPatchData(long id, String userId);

    long patchBoard(BoardRequest request, long id, String userId);

    void deleteBoard(long id, String userId);

    void getReplyInfo(long id);

    long insertBoardReply(long targetId, BoardReplyRequest request, String userId);

}
