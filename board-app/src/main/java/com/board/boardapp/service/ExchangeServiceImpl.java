package com.board.boardapp.service;

import com.board.boardapp.ExceptionHandle.CustomAccessDeniedException;
import com.board.boardapp.ExceptionHandle.CustomTokenStealingException;
import com.board.boardapp.ExceptionHandle.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService{

    private final CookieService cookieService;

    @Override
    public void checkExchangeResponse(ClientResponse res, HttpServletResponse response) {

        System.out.println("checkExchangeResponse status code : " + res.statusCode());

        if(res.statusCode().equals(HttpStatus.OK)){
            cookieService.setCookie(res, response);
        }else if(res.statusCode().equals(HttpStatus.FORBIDDEN)){
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, "AccessDenied");
        }else if(res.rawStatusCode() == 800){
            //토큰 탈취
            cookieService.setCookie(res, response);
            throw new CustomTokenStealingException(ErrorCode.TOKEN_STEALING);
        }else if(res.statusCode().is4xxClientError()){
            throw new RuntimeException();
        }else if(res.statusCode().is5xxServerError()){
            throw new NullPointerException();
        }

    }
}
