package com.board.boardapp.service;

import com.board.boardapp.ExceptionHandle.CustomTokenStealingException;
import com.board.boardapp.ExceptionHandle.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService{

    private final CookieService cookieService;

    @Override
    public void checkExchangeResponse(ClientResponse res, HttpServletResponse response) {
        if(res.statusCode().equals("800")){//탈취
            cookieService.setCookie(res, response);
            new CustomTokenStealingException(ErrorCode.TOKEN_STEALING);
        }else if(res.statusCode().equals(HttpStatus.OK)){
            cookieService.setCookie(res,response);
            // 성공했다고 리턴을 해야할지?
        }else if(res.statusCode().equals("403")){
            new AccessDeniedException("AccessDenied");
        }else if(res.statusCode().is4xxClientError()){
            // custom exception
            new RuntimeException();
        }else if(res.statusCode().is5xxServerError()){
            //custom exception
            new NullPointerException();
        }
    }
}
