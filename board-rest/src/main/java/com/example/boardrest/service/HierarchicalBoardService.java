package com.example.boardrest.service;

import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.dto.responseDTO.ResponseDetailAndModifyDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;

import java.security.Principal;

public interface HierarchicalBoardService {

    long insertBoard(HierarchicalBoardDTO dto, Principal principal);

    long insertBoardReply(HierarchicalBoardReplyDTO dto, Principal principal);

    long deleteBoard(long boardNo, Principal principal);

//    Page<HierarchicalBoardListDTO> getHierarchicalBoardList(Criteria cri, Principal principal);

    ResponsePageableListDTO getHierarchicalBoardList(Criteria cri, Principal principal);

    long patchBoard(HierarchicalBoardModifyDTO dto, Principal principal);

    ResponseDetailAndModifyDTO<HierarchicalBoardModifyDTO> getModifyData(long boardNo, Principal principal);

    ResponseDetailAndModifyDTO<HierarchicalBoardDetailDTO> getBoardDetail(long boardNo, Principal principal);

    ResponseDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> getReplyInfo(long boardNo, Principal principal);
}
