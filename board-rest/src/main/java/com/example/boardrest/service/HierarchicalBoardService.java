package com.example.boardrest.service;

import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.HierarchicalBoard;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

public interface HierarchicalBoardService {

    long insertBoard(HttpServletRequest request, Principal principal);

    long insertBoardReply(HttpServletRequest request, Principal principal);

    long deleteBoard(long boardNo);

    Page<HierarchicalBoardDTO> getHierarchicalBoardList(Criteria cri);

    long patchBoard(HttpServletRequest request);

}
