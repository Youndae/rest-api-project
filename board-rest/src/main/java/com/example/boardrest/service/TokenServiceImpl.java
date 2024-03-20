package com.example.boardrest.service;

import com.example.boardrest.domain.dto.JwtDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    private final JwtTokenProvider provider;

    @Override
    public JwtDTO reIssuedToken(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> reIssuedData = provider.verifyRefreshToken(request);
        JwtDTO dto = null;

        if(reIssuedData != null)
            dto = provider.reIssuanceAllToken(reIssuedData, response);

        return dto;
    }
}
