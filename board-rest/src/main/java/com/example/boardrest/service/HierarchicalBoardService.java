package com.example.boardrest.service;

import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardDetailDTO;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardListDTO;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardReplyInfoDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardModifyDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardReplyDTO;
import com.example.boardrest.domain.dto.paging.Criteria;
import org.springframework.data.domain.Page;

import java.security.Principal;

public interface HierarchicalBoardService {

    Page<HierarchicalBoardListDTO> getHierarchicalBoardList(Criteria cri);

    HierarchicalBoardDetailDTO getBoardDetail(long boardNo);

    long insertBoard(HierarchicalBoardDTO dto, Principal principal);

    HierarchicalBoardModifyDTO getModifyData(long boardNo, Principal principal);

    long patchBoard(HierarchicalBoardModifyDTO dto, long boardNo, Principal principal);

    String deleteBoard(long boardNo, Principal principal);

    HierarchicalBoardReplyInfoDTO getReplyInfo(long boardNo);

    long insertBoardReply(HierarchicalBoardReplyDTO dto, Principal principal);

}
