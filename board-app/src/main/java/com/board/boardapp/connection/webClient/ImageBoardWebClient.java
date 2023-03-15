package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageBoardWebClient {

    private final WebClientConfig webClientConfig;

    private final TokenService tokenService;
}
