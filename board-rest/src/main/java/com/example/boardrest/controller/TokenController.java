package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.service.JwtTokenProvider;
import com.example.boardrest.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/token")
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class TokenController {

    private final JwtTokenProvider provider;

    private final TokenService tokenService;

    /*@PostMapping("/reissued")
    public ResponseEntity<JwtDTO> reissuedToken(HttpServletRequest request, HttpServletResponse response){

//      return new ResponseEntity<>(tokenService.reIssuedToken(request, response), HttpStatus.OK);
    }*/
}
