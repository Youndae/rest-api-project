package com.example.boardrest.service;

import com.example.boardrest.domain.HierarchicalBoard;

import javax.servlet.http.HttpServletRequest;

public interface HierarchicalBoardService {

    void insertBoard(HierarchicalBoard hierarchicalBoard, HttpServletRequest request);

    void insertBoardReply(HierarchicalBoard hierarchicalBoard, HttpServletRequest request);

    void deleteBoard(HierarchicalBoard hierarchicalBoard);
}
