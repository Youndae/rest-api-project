package com.example.boardrest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.dto.JwtDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final StringRedisTemplate redisTemplate;

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



    //verify RefreshToken
    public Map<String, String> verifyRefreshToken(HttpServletRequest request){

        Cookie refreshToken = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);
        Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

        //rt가 존재하더라도 ino가 존재하지 않는다면 rt가 탈취되었다고 판단 null을 리턴한다.
        //굳이 해당 rt에 대한 처리를 하지 않은 이유로는 ino가 없다면 어차피 rt만으로 아무것도 할 수 없기 때문.
        if(refreshToken == null || !refreshToken.getValue().startsWith(JwtProperties.TOKEN_PREFIX)) {
            log.info("refresh Token null");
            return null;
        }else if(ino == null){
            return null;
        }

        /*
            토큰 상태가 정상이고 ino가 존재한다면
            1. verify를 통해 claim인 userId를 꺼내고
            2. "rt" + tno + userId의 value값과 refreshTokenVal을 비교한다.
            3. 둘이 일치한다면
                3-1. "at" + tno + userId가 존재하는지 여부를 체크
                    3-1-1. 존재한다면 expire 리턴값이 60보다 큰 경우에는 rt, at 토큰 삭제 후 null 리턴

                3-2. at가 존재하지 않거나 존재하면서 expire 리턴값이 60보다 작은 경우에는 map 데이터를 리턴
            4. 일치하지 않는다면 해당 키값의 토큰을 모두 삭제

         */

        String refreshTokenVal = refreshToken.getValue().replace(JwtProperties.TOKEN_PREFIX, "");
        String inoVal = ino.getValue();

        String userId = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build()
                .verify(refreshTokenVal)
                .getClaim("userId")
                .asString();

        if(refreshTokenVal != null && userId != null){
            String rtKey = "rt" + inoVal + userId;
            String atKey = "at" + inoVal + userId;

            if(checkRefreshToken(rtKey, refreshTokenVal)){
                if(checkAccessToken(atKey)){
                    Map<String, String> tokenMap = new HashMap<>();
                    tokenMap.put("userId", userId);
                    tokenMap.put("ino", inoVal);

                    return tokenMap;
                }else{
                    deleteTokenData(inoVal, userId);
                }
            }
        }
        return null;
    }

    public boolean checkAccessToken(String atKey){
        long keyExpire = redisTemplate.getExpire(atKey);

        //at가 -2로 삭제된 데이터이거나 60보다 작아 1분 미만으로 만료기간이 남았다면
        if(keyExpire == -2 || keyExpire < 60)
            return true;
        else
            return false;

    }

    public boolean checkRefreshToken(String rtKey, String tokenValue){
        ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();
        String value = stringValueOperations.get(rtKey);

        if(value != null && value.equals(tokenValue))
            return true;
        else
            return false;
    }


    //reissuance AccessToken & RefreshToken
    public JwtDTO reIssuanceAllToken(Map<String, String> reIssuedData){

        log.info("reIssuanceAllToken");

        /*
            reIssuedData = {
                            userId : value,
                            ino : value
                            }
         */

        String userId = reIssuedData.get("userId");
        String ino = reIssuedData.get("ino");

        JwtDTO dto = JwtDTO.builder()
                .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                .accessTokenValue(JwtProperties.TOKEN_PREFIX + issuedAccessToken(userId, ino))
                .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                .refreshTokenValue(JwtProperties.TOKEN_PREFIX + issuedRefreshToken(userId, ino))
                .inoHeader(JwtProperties.INO_HEADER_STRING)
                .inoValue(ino)
                .build();

        return dto;
    }



    public JwtDTO loginProcIssuedAllToken(String userId, HttpServletRequest request){

        JwtDTO dto = null;

        Cookie tnoCookie = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);
        String ino = null;

        /*
            로그인 요청이 발생.
            if(tno == null)
                최초 로그인 디바이스로 tno 생성과 각 토큰을 생성.
            else if(tno != null)
                if(checkToken)
                    해당 토큰이 존재하지 않기 때문에 토큰 생성 후 로그인 처리
                else
                    해당 토큰이 존재하기 때문에 토큰 데이터 삭제 tno를 재생성하고 토큰 생성 및 로그인 처리

         */

        if(tnoCookie == null)
            ino = issuedTno();
        else {
            if(tokenExistence(tnoCookie.getValue(), userId))
                ino = tnoCookie.getValue();
            else {
                deleteTokenData(tnoCookie.getValue(), userId);
                ino = issuedTno();
            }
        }


        dto =  JwtDTO.builder()
                .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                .accessTokenValue(JwtProperties.TOKEN_PREFIX + issuedAccessToken(userId, ino))
                .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                .refreshTokenValue(JwtProperties.TOKEN_PREFIX + issuedRefreshToken(userId, ino))
                .inoHeader(JwtProperties.INO_HEADER_STRING)
                .inoValue(ino)
                .build();


        return dto;
    }

    public void deleteTokenData(String ino, String userId){
        String rtKey = JwtProperties.REFRESH_TOKEN_PREFIX + ino + userId;
        String atKey = JwtProperties.ACCESS_TOKEN_PREFIX + ino + userId;

        redisTemplate.delete(rtKey);
        redisTemplate.delete(atKey);
    }

    public boolean tokenExistence(String ino, String userId){

        ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();
        String key = "rt" + ino + userId;

        String value = stringValueOperations.get(key);

        if(value == null)
            return true;
        else
            return false;

    }

    public String issuedTno(){

        return UUID.randomUUID().toString().replace("-", "");
    }

    //issued AccessToken
    public String issuedAccessToken(String userId, String ino){

        String accessToken = JWT.create()
                .withSubject("cocoToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_EXPIRATION_TIME))
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        log.info("issuedAccessToken accessToken : {}", accessToken);

        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
        stringStringValueOperations.set(JwtProperties.ACCESS_TOKEN_PREFIX + ino + userId, accessToken, Duration.ofHours(2L));

        return accessToken;
    }


    //issued RefreshToken
    public String issuedRefreshToken(String userId, String ino){

        String refreshToken = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_EXPIRATION_TIME))
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
        stringStringValueOperations.set(JwtProperties.REFRESH_TOKEN_PREFIX + ino + userId, refreshToken, Duration.ofDays(14L));

        return refreshToken;
    }

}
