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
    public String verifyAccessToken(Cookie accessToken, Cookie ino){
       String tokenVal = accessToken.getValue().replace(JwtProperties.TOKEN_PREFIX, "");
       String inoVal = ino.getValue();
       String claimByUserId = getClaimUserIdByToken(tokenVal);

       if(claimByUserId == null)
           return null;

       String atKey = "at" + inoVal + claimByUserId;
       String redisAtValue = redisTemplate.opsForValue().get(atKey);

       if(redisAtValue == null || !tokenVal.equals(redisAtValue))
           return null;

        return claimByUserId;
    }



    //verify RefreshToken
    public Map<String, String> verifyRefreshToken(HttpServletRequest request){

        Cookie refreshToken = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);
        Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

        //rt가 존재하더라도 ino가 존재하지 않는다면 rt가 탈취되었다고 판단 null을 리턴한다.
        //굳이 해당 rt에 대한 처리를 하지 않은 이유로는 ino가 없다면 어차피 rt만으로 아무것도 할 수 없기 때문.
        if(refreshToken == null || !refreshToken.getValue().startsWith(JwtProperties.TOKEN_PREFIX))
            return null;
        else if(ino == null)
            return null;


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
        String claimByUserId = getClaimUserIdByToken(refreshTokenVal);

        if(refreshTokenVal != null && claimByUserId != null){
            String rtKey = "rt" + inoVal + claimByUserId;
            String atKey = "at" + inoVal + claimByUserId;

            if(checkRefreshToken(rtKey, refreshTokenVal)){
                if(checkAccessToken(atKey)){
                    Map<String, String> tokenMap = new HashMap<>();
                    tokenMap.put("userId", claimByUserId);
                    tokenMap.put("ino", inoVal);

                    return tokenMap;
                }else{
                    deleteTokenData(inoVal, claimByUserId);
                }
            }
        }
        return null;
    }

    //token에서 Claim으로 설정된 userId를 꺼내 반환.
    public String getClaimUserIdByToken(String tokenValue) {

        return JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                    .build()
                    .verify(tokenValue)
                    .getClaim("userId")
                    .asString();
    }

    public boolean checkAccessToken(String atKey){
        long keyExpire = redisTemplate.getExpire(atKey);

        log.info("AccessToken keyExpire : {}", keyExpire);

        // 클라이언트는 AccessToken의 만료시간이 1분 적기 때문에
        // keyExpire가 -2로 삭제된 데이터이거나 60보다 작아 1분 미만으로 만료기간이 남았다면
        // 탈취된 토큰 또는 만료된 토큰으로 판단할 수 있다.
        if(keyExpire == -2 || keyExpire < 60) {
            log.info("AccessToekn Expire return false");
            return false;
        }else {
            log.info("AccessToken Expire return true");
            return true;
        }
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
        /**
         * @Param
         * reIssuedData = {
         *                  userId : value,
         *                  ino : value
         *               }
         */

        log.info("reIssuanceAllToken");

        String userId = reIssuedData.get("userId");
        String ino = reIssuedData.get("ino");

        return buildJwtDTO(userId, ino);
    }



    public JwtDTO loginProcIssuedAllToken(String userId, HttpServletRequest request){

        Cookie inoCookie = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);
        String ino = null;

        /*
            로그인 요청이 발생.
            if(ino == null)
                최초 로그인 디바이스로 tno 생성과 각 토큰을 생성.
            else if(ino != null)
                if(checkToken)
                    해당 토큰이 존재하지 않기 때문에 토큰 생성 후 로그인 처리
                else
                    해당 토큰이 존재하기 때문에 토큰 데이터 삭제 tno를 재생성하고 토큰 생성 및 로그인 처리

         */

        if(inoCookie == null)
            ino = issuedIno();
        else {
            if(tokenExistence(inoCookie.getValue(), userId))
                ino = inoCookie.getValue();
            else {
                deleteTokenData(inoCookie.getValue(), userId);
                ino = issuedIno();
            }
        }

        return buildJwtDTO(userId, ino);
    }

    public JwtDTO buildJwtDTO(String userId, String ino) {

        return JwtDTO.builder()
                    .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                    .accessTokenValue(JwtProperties.TOKEN_PREFIX + issuedAccessToken(userId, ino))
                    .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                    .refreshTokenValue(JwtProperties.TOKEN_PREFIX + issuedRefreshToken(userId, ino))
                    .inoHeader(JwtProperties.INO_HEADER_STRING)
                    .inoValue(ino)
                    .build();
    }

    public void deleteTokenData(String ino, String userId){
        String rtKey = JwtProperties.REFRESH_TOKEN_PREFIX + ino + userId;
        String atKey = JwtProperties.ACCESS_TOKEN_PREFIX + ino + userId;

        log.info("deleteTokenData");

        redisTemplate.delete(rtKey);
        redisTemplate.delete(atKey);

        log.info("deleteToken Success");
    }

    public boolean tokenExistence(String ino, String userId){
        String key = "rt" + ino + userId;
        ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();
        String value = stringValueOperations.get(key);

        //Redis에 해당 key가 존재하지 않는다면 true를 리턴, 존재한다면 false를 리턴
        if(value == null)
            return true;
        else
            return false;
    }

    public String issuedIno(){

        return UUID.randomUUID().toString().replace("-", "");
    }

    //issued AccessToken
    public String issuedAccessToken(String userId, String ino){
        String accessToken = JWT.create()
                .withSubject("cocoToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_EXPIRATION_TIME))
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

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
