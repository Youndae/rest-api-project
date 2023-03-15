package com.example.boardrest.service;

import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.HierarchicalBoard;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.HierarchicalBoardModifyDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

public interface HierarchicalBoardService {

    long insertBoard(HierarchicalBoardDTO dto, Principal principal);

    long insertBoardReply(HierarchicalBoardModifyDTO dto, Principal principal);

    void deleteBoard(long boardNo);

    Page<HierarchicalBoardDTO> getHierarchicalBoardList(Criteria cri);

    long patchBoard(HierarchicalBoardModifyDTO dto, Principal principal);

    HierarchicalBoardModifyDTO getModifyData(long boardNo, Principal principal);


}
