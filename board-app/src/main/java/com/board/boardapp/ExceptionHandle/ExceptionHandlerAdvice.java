package com.board.boardapp.ExceptionHandle;

import com.board.boardapp.domain.dto.LoginDTO;
import com.board.boardapp.domain.dto.UserStatusDTO;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    private static final LoginDTO dto = new LoginDTO(new UserStatusDTO(false));

    @ExceptionHandler(CustomBadCredentialsException.class)
    public ResponseEntity customBadCredentialsHandler(Exception e) {

        log.info("BadCredentialsException");


        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @ExceptionHandler(NotFoundException.class)
    public String notFoundHandle(NotFoundException e, Model model){
        log.info("NotFountException : " + e);

        model.addAttribute("data", dto);

        return "th/error/error";
    }


    @ExceptionHandler(NullPointerException.class)
    public String exceptionHandle(Exception e, Model model){
        log.info("NullPointerException e : " + e.getMessage());

        model.addAttribute("data", dto);
        return "th/error/error";
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public String customAccessDeniedHandle(Exception e){
        log.info("CustomAccessDeniedException e : " + e.getMessage());

        return "redirect:/member/login";
    }


    @ExceptionHandler(CustomNotFoundException.class)
    public String customNotFoundExceptionResponseEntity(CustomNotFoundException e, Model model){
        log.info("CustomNotFoundException");

        model.addAttribute("data", dto);

        return "th/error/error";
    }

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity sizeLimitExceededExceptionHandler(SizeLimitExceededException e){

        log.info("size limit exception");

        Map<String, String> result = new HashMap<>();
        result.put("message", "Large File");

        return ResponseEntity.status(HttpStatus.valueOf(400))
                .body(result);
    }

    @ExceptionHandler(CustomTokenStealingException.class)
    public String tokenStealingExceptionHandler(CustomTokenStealingException e) {
        log.info("TokenStealingException : " + e.getMessage());

        return "th/member/userError";
    }



}
