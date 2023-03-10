package com.example.boardrest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.RefreshToken;
import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.domain.dto.RefreshDTO;
import com.example.boardrest.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    //issued AccessToken
    public String issuedAccessToken(String userId){

        String accessToken = JWT.create()
                .withSubject("cocoToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_EXPIRATION_TIME))
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        return accessToken;
    }


    //issued RefreshToken
    public String issuedRefreshToken(String userId){

        RefreshDTO dto = createRefreshToken();

        refreshTokenRepository.save(RefreshToken.builder()
                .rtIndex(dto.getRefreshIndex())
                .userId(userId)
                .expires(dto.getTokenExpires())
                .tokenVal(dto.getTokenVal())
                .build());

        return dto.getTokenVal();
    }

    public String reIssuedRefreshToken(String originIndex){
        RefreshDTO dto = createRefreshToken();

        refreshTokenRepository.patchToken(dto.getTokenVal()
                                        , dto.getTokenExpires()
                                        , dto.getRefreshIndex()
                                        , originIndex
                                    );

        return dto.getTokenVal();
    }

    public RefreshDTO createRefreshToken(){

        StringBuilder sb = new StringBuilder();

        String rIndex = sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()))
                .append(UUID.randomUUID()).toString();

        Date refreshExpires = new Date(System.currentTimeMillis() + JwtProperties.REFRESH_EXPIRATION_TIME);

        String refreshToken = JWT.create()
                .withExpiresAt(refreshExpires)
                .withClaim("refresh", rIndex)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        RefreshDTO refreshDTO = RefreshDTO.builder()
                .refreshIndex(rIndex)
                .tokenVal(refreshToken)
                .tokenExpires(refreshExpires)
                .build();

        return refreshDTO;
    }


    //verify AccessToken
    public String verifyAccessToken(Cookie accessToken){

        log.info("verify AccessToken");

       String tokenVal = accessToken.getValue().replace(JwtProperties.TOKEN_PREFIX, "");

       log.info("verify token value : " + tokenVal);

       String claimByUserId = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
               .build()
               .verify(tokenVal)
               .getClaim("userId")
               .asString();

       log.info("claim userId : " + claimByUserId);

        return claimByUserId;
    }


    /**
     * AccessToken ???????????? refreshToken??? ?????? ??????.
     * accessToken??? refreshToken??? ??????.
     * ??? ????????? ?????? ??????.
     * ?????? ????????? ???????????? AccessToken??? ????????? ????????? ?????? userId??? ????????? ??? ?????????.
     * 1. ????????? ????????????
     *      ?????? ???????????? refreshToken ????????? ??????.
     * 2. ????????? ?????????
     *      ???????????? ???????????? refreshToken ????????? ??????.
     * ?????? ????????? ??? ???
     * ?????? null?????? ???????????? ?????? ?????? existsToken?????? DB ???????????? ??????.
     *
     * DB ???????????? ??????????????? ?????? ?????????.
     * JwtDTO??? ????????? ????????? ??????.
     * client??? ????????? ?????? ??? ??? ?????? ??? ????????? ????????? ???????????? ??????.
     */
    //verify RefreshToken & AccessToken
    public JwtDTO verifyRefreshToken(HttpServletRequest request){

        Cookie refreshToken = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);

        if(refreshToken == null || !refreshToken.getValue().startsWith(JwtProperties.TOKEN_PREFIX))
            return null;

        String refreshTokenVal = refreshToken.getValue().replace(JwtProperties.TOKEN_PREFIX, "");

        String rIndex = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build()
                .verify(refreshTokenVal)
                .getClaim("refresh")
                .asString();

        //refreshToken ????????? ????????? userId??? ??????.
        //?????? query??? select userId from refreshToken where rtIndex = rIndex And tokenVal = refreshTokenVal
        // ????????? ????????? userId??? null??? ?????? ???????????? ?????? ?????????.
        String userId = refreshTokenRepository.existsByRtIndexAndUserId(rIndex, refreshTokenVal);

        JwtDTO dto = new JwtDTO();

        if(userId != null)
            dto = reIssuanceAllToken(userId, rIndex);

        return dto;
    }


    //reissuance AccessToken & RefreshToken
    public JwtDTO reIssuanceAllToken(String userId, String originIndex){

        return JwtDTO.builder()
                .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                .accessTokenValue(issuedAccessToken(userId))
                .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                .refreshTokenValue(reIssuedRefreshToken(originIndex))
                .build();
    }
}
