package com.example.boardrest.controller;

import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/token")
@Slf4j
@RequiredArgsConstructor
public class TokenController {

    private final JwtTokenProvider provider;

    @GetMapping("/reissued")
    public ResponseEntity<JwtDTO> reissuedToken(HttpServletRequest request){
      log.info("reissued Token");

      return new ResponseEntity<>(provider.verifyRefreshToken(request), HttpStatus.OK);
    }
}
