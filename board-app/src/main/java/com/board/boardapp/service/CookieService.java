package com.board.boardapp.service;

import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CookieService {

    MultiValueMap<String, String> setCookieToMultiValueMap(HttpServletRequest request);

    void setCookie(ClientResponse res, HttpServletResponse response);
}
