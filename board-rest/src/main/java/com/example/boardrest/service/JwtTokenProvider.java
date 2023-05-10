package com.example.boardrest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.entity.RefreshToken;
import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.domain.dto.RefreshDTO;
import com.example.boardrest.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

        log.info("issuedAccessToken accessToken : {}", accessToken);

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

        log.info("reIssuedRefreshToken");
        RefreshDTO dto = createRefreshToken();

        refreshTokenRepository.patchToken(dto.getTokenVal()
                                        , dto.getTokenExpires()
                                        , dto.getRefreshIndex()
                                        , originIndex
                                    );

        log.info("getTokenVal : {}", dto.getTokenVal());

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
     * AccessToken 만료시에 refreshToken과 같이 검증.
     * accessToken과 refreshToken을 받고.
     * 두 토큰을 모두 검증.
     * 이때 테스트 필요한게 AccessToken이 만료된 토큰인 경우 userId를 식별할 수 있는가.
     * 1. 식별이 안된다면
     *      다른 방법으로 refreshToken 검증이 필요.
     * 2. 식별이 된다면
     *      계획했던 방법대로 refreshToken 검증이 가능.
     * 토큰 검증을 한 뒤
     * 둘다 null값이 아니라는 조건 하에 existsToken으로 DB 데이터와 대조.
     *
     * DB 데이터와 동일하다면 둘다 재발급.
     * JwtDTO에 담아서 리턴이 필요.
     * client로 토큰을 전달 한 뒤 다시 재 요청이 오도록 할것이기 때문.
     */
    //verify RefreshToken & AccessToken
    public Map<String, String> verifyRefreshToken(HttpServletRequest request){

        Cookie refreshToken = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);

        if(refreshToken == null || !refreshToken.getValue().startsWith(JwtProperties.TOKEN_PREFIX)) {
            log.info("refresh Token null");
            return null;
        }

        String refreshTokenVal = refreshToken.getValue().replace(JwtProperties.TOKEN_PREFIX, "");

        String rIndex = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build()
                .verify(refreshTokenVal)
                .getClaim("refresh")
                .asString();

        log.info("tokenval : {}, rIndex : {}", refreshTokenVal, rIndex);

        if(refreshTokenVal != null && rIndex != null){
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("rIndex", rIndex);
            tokenMap.put("refreshTokenValue", refreshTokenVal);

            return tokenMap;
        }

        return null;
    }


    //reissuance AccessToken & RefreshToken
    public JwtDTO reIssuanceAllToken(Map<String, String> reIssuedData){

        log.info("reIssuanceAllToken");

        //refreshToken 검증과 동시에 userId를 획득.
        //그럼 query는 select userId from refreshToken where rtIndex = rIndex And tokenVal = refreshTokenVal
        // 이렇게 처리해 userId가 null인 경우 재발급을 하지 않도록.
        String userId = refreshTokenRepository.existsByRtIndexAndUserId(
                            reIssuedData.get("rIndex")
                            , reIssuedData.get("refreshTokenValue")
                        );

        log.info("reIssued userId : {}", userId);

        if(userId != null){
            JwtDTO dto =  JwtDTO.builder()
                    .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                    .accessTokenValue(JwtProperties.TOKEN_PREFIX + issuedAccessToken(userId))
                    .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                    .refreshTokenValue(JwtProperties.TOKEN_PREFIX + reIssuedRefreshToken(reIssuedData.get("rIndex")))
                    .build();

            log.info("reIssuanceAllToken Data : {} : {} \n {} : {}", dto.getAccessTokenHeader(), dto.getAccessTokenValue()
                    , dto.getRefreshTokenHeader(), dto.getRefreshTokenValue());

            return dto;
        }

        return null;
    }




    /*//verify RefreshToken & AccessToken
    public JwtDTO verifyRefreshToken(HttpServletRequest request){

        Cookie refreshToken = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);

        if(refreshToken == null || !refreshToken.getValue().startsWith(JwtProperties.TOKEN_PREFIX)) {
            log.info("refresh Token null");
            return null;
        }

        String refreshTokenVal = refreshToken.getValue().replace(JwtProperties.TOKEN_PREFIX, "");

        String rIndex = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build()
                .verify(refreshTokenVal)
                .getClaim("refresh")
                .asString();

        log.info("tokenval : {}, rIndex : {}", refreshTokenVal, rIndex);

        //refreshToken 검증과 동시에 userId를 획득.
        //그럼 query는 select userId from refreshToken where rtIndex = rIndex And tokenVal = refreshTokenVal
        // 이렇게 처리해 userId가 null인 경우 재발급을 하지 않도록.
        String userId = refreshTokenRepository.existsByRtIndexAndUserId(rIndex, refreshTokenVal);

        log.info("userId : {}", userId);

        JwtDTO dto = new JwtDTO();

        if(userId != null)
            dto = reIssuanceAllToken(userId, rIndex);

        log.info("dto : {} : {} \n {} : {}", dto.getAccessTokenHeader(), dto.getAccessTokenValue()
        , dto.getRefreshTokenHeader(), dto.getRefreshTokenValue());

        return dto;
    }


    //reissuance AccessToken & RefreshToken
    public JwtDTO reIssuanceAllToken(String userId, String originIndex){

        log.info("reIssuanceAllToken");

        JwtDTO dto =  JwtDTO.builder()
                .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                .accessTokenValue(JwtProperties.TOKEN_PREFIX + issuedAccessToken(userId))
                .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                .refreshTokenValue(JwtProperties.TOKEN_PREFIX + reIssuedRefreshToken(originIndex))
                .build();

        log.info("reIssuanceAllToken Data : {} : {} \n {} : {}", dto.getAccessTokenHeader(), dto.getAccessTokenValue()
        , dto.getRefreshTokenHeader(), dto.getRefreshTokenValue());

        return dto;
    }*/
}
