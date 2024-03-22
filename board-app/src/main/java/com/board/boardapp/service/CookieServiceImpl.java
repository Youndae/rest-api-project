package com.board.boardapp.service;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Service
public class CookieServiceImpl implements CookieService{

    @Override
    public MultiValueMap<String, String> setCookieToMultiValueMap(HttpServletRequest request) {
        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<String, String>();

        Arrays.stream(request.getCookies()).filter(idx -> idx.getName().startsWith("Authorization"))
                .forEach(cookie -> mvm.add(cookie.getName(), cookie.getValue()));


        return mvm;
    }

    @Override
    public void setCookie(ClientResponse res, HttpServletResponse response) {
        if(!res.cookies().isEmpty()) {
            res.cookies()
                    .forEach(
                            (k, v) -> response.addHeader("Set-Cookie", v.get(0).toString())
                    );
        }
    }
}
