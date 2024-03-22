package com.board.boardapp.service;

import org.springframework.web.reactive.function.client.ClientResponse;

import javax.servlet.http.HttpServletResponse;

public interface ExchangeService {

    void checkExchangeResponse(ClientResponse res, HttpServletResponse response);
}
