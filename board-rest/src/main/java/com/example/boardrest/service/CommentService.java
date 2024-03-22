package com.example.boardrest.service;

import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.CommentListDTO;
import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.CommentInsertDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;

public interface CommentService {

    long commentInsertProc(CommentInsertDTO dto, Principal principal);

    int commentDelete(long commentNo, Principal principal);

    ResponsePageableListDTO<BoardCommentDTO> commentList(String boardNo, String imageNo, Criteria cri, Principal principal);
}
