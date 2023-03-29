package com.example.boardrest.service;

import com.example.boardrest.repository.CountTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountTableServiceImpl implements CountTableService{

    private final CountTableRepository countTableRepository;

    @Override
    public void boardCountPlus(String boardName) {

        long countVal = boardCountValue(boardName) + 1;

        countTableRepository.BoardCountUpdate(countVal, boardName);

    }

    @Override
    public void boardCountMinus(String boardName) {

        long countVal = boardCountValue(boardName) - 1;

        countTableRepository.BoardCountUpdate(countVal, boardName);

    }

    public long boardCountValue(String boardName){
        return countTableRepository.boardCountValue(boardName);
    }
}
