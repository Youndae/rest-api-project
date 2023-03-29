package com.example.boardrest.service;

import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.HierarchicalBoardModifyDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;

public interface HierarchicalBoardService {

    long insertBoard(HierarchicalBoardDTO dto, Principal principal);

    long insertBoardReply(HierarchicalBoardModifyDTO dto, Principal principal);

    void deleteBoard(long boardNo);

    Page<HierarchicalBoardDTO> getHierarchicalBoardList(Criteria cri) ;

    long patchBoard(HierarchicalBoardModifyDTO dto, Principal principal);

    HierarchicalBoardModifyDTO getModifyData(long boardNo, Principal principal);


}
