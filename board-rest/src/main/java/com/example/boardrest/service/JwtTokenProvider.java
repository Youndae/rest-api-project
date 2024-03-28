package com.example.boardrest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.boardrest.config.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final StringRedisTemplate redisTemplate;



    public String decodeToken(Cookie tokenCookie) {
        String tokenValue = tokenCookie.getValue().replace(JwtProperties.TOKEN_PREFIX, "");

        return JWT.decode(tokenValue)
                .getClaim("userId")
                .asString();
    }

    /**
     * @param accessToken
     * @param inoValue
     * @return
     *
     * accessToken, refreshToken 검증에서 모두 만료되지 않은 토큰이라면 redis 데이터도 남아있으므로 데이터를 비교한다.
     * 데이터가 존재하지 않는다면 null을 반환한다.
     * 데이터가 존재하는데 일치하지 않는다면 "st"를 반환해 탈취를 알린다.
     *
     * 만료된 토큰이라면 "Token Expiration" 을 반환해 만료되었다는 것을 알린다.
     *
     */
    public String verifyAccessToken(Cookie accessToken, String inoValue) {
        String accessTokenValue = accessToken.getValue().replace(JwtProperties.TOKEN_PREFIX, "");
        String accessClaimByUserId = getClaimUserIdByToken(accessTokenValue, JwtProperties.ACCESS_SECRET);

        //잘못된 토큰인 경우 null이 반환될 것이고,
        //만료된 토큰이라면 Exception이 발생해 예외처리로 인해 TOKEN_EXPIRATION_RESULT가 반환된다.
        if (accessClaimByUserId == null)
            return null;
        else if(accessClaimByUserId.equals(JwtProperties.TOKEN_EXPIRATION_RESULT))
            return JwtProperties.TOKEN_EXPIRATION_RESULT;
        else if(accessClaimByUserId.equals(JwtProperties.WRONG_TOKEN))
            return JwtProperties.WRONG_TOKEN;

        String accessTokenKey = JwtProperties.ACCESS_TOKEN_PREFIX + inoValue + accessClaimByUserId;
        String redisValue = getTokenValueData(accessTokenKey);

        if(accessTokenValue.equals(redisValue))
            return accessClaimByUserId;
        else {
            deleteTokenData(accessClaimByUserId, inoValue);
            return JwtProperties.TOKEN_STEALING_RESULT;
        }
    }

    public String verifyRefreshToken(Cookie refreshToken, String inoValue, String accessTokenClaim) {
         String refreshTokenValue = refreshToken.getValue().replace(JwtProperties.TOKEN_PREFIX, "");
         String refreshClaimByUserId = getClaimUserIdByToken(refreshTokenValue, JwtProperties.REFRESH_SECRET);

         if(refreshClaimByUserId == null)
             return null;
         else if(refreshClaimByUserId.equals(JwtProperties.TOKEN_EXPIRATION_RESULT))
             return JwtProperties.TOKEN_EXPIRATION_RESULT;
         else if(refreshClaimByUserId.equals(JwtProperties.WRONG_TOKEN))
             return JwtProperties.WRONG_TOKEN;
         else if(!refreshClaimByUserId.equals(accessTokenClaim)){
             deleteTokenData(refreshClaimByUserId, inoValue);
             return JwtProperties.TOKEN_STEALING_RESULT;
         }

         String refreshTokenKey = JwtProperties.REFRESH_TOKEN_PREFIX + inoValue + refreshClaimByUserId;
         String redisValue = getTokenValueData(refreshTokenKey);

         if(refreshTokenValue.equals(redisValue))
             return refreshClaimByUserId;
         else {
             deleteTokenData(refreshClaimByUserId, inoValue);
             return JwtProperties.TOKEN_STEALING_RESULT;
         }
    }

    public String getTokenValueData(String tokenKey) {
        long keyExpire = redisTemplate.getExpire(tokenKey);

        //Token이 존재하는데 -2라면 Redis 데이터가 만료되어 삭제된 것이기 때문에
        //null을 반환한다.
        if(keyExpire == -2)
            return null;

        return redisTemplate.opsForValue().get(tokenKey);
    }

    //token에서 Claim으로 설정된 userId를 꺼내 반환.
    public String getClaimUserIdByToken(String tokenValue, String secret) {

        try {
            String claimByUserId = JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(tokenValue)
                    .getClaim("userId")
                    .asString();

            return claimByUserId;
        }catch(TokenExpiredException e) {
            //토큰 만료 exception
            return JwtProperties.TOKEN_EXPIRATION_RESULT;
        }catch (JWTDecodeException e) {
            //비정상 토큰. 검증할 수 없는 토큰.
            return JwtProperties.WRONG_TOKEN;
        }
    }

    //ino를 제외한 accessToken, refreshToken 생성
    public void issuedToken(String userId, String inoValue, HttpServletResponse response) {
        String accessToken = issuedAccessToken(userId, inoValue);
        String refreshToken = issuedRefreshToken(userId, inoValue);

        //쿠키 생성 메소드 호출 (@Param accessToken, refreshToken, inoValue, response)
        setTokenToCookie(accessToken, refreshToken, response);
    }

    //ino까지 모두 생성
    public void issuedAllToken(String userId, HttpServletResponse response) {
        String inoValue = issuedIno();
        issuedToken(userId, inoValue, response);

        //쿠키 생성 메소드 호출 (@Param accessToken, refreshToken, inoValue, response)
//        setTokenToCookie(accessToken, refreshToken, response);
        setInoToCookie(inoValue, response);
    }

    public String issuedAccessToken(String userId, String inoValue) {
        String accessToken = createToken(userId, JwtProperties.ACCESS_SECRET, JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME);
        String key = JwtProperties.ACCESS_TOKEN_PREFIX + inoValue + userId;

        setRedisByToken(key, accessToken);

        return JwtProperties.TOKEN_PREFIX + accessToken;
    }

    public String issuedRefreshToken(String userId, String inoValue) {
        String refreshToken = createToken(userId, JwtProperties.REFRESH_SECRET, JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME);
        String key = JwtProperties.REFRESH_TOKEN_PREFIX + inoValue + userId;

        setRedisByToken(key, refreshToken);

        return JwtProperties.TOKEN_PREFIX + refreshToken;
    }

    public String issuedIno(){

        return UUID.randomUUID().toString().replace("-", "");
    }

    public String createToken(String userId, String secret, long expirationTime) {
        return JWT.create()
                .withSubject("cocoToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC512(secret));
    }

    public void setRedisByToken(String key, String value) {
        ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();
        stringValueOperations.set(key, value, JwtProperties.REDIS_TOKEN_DATA_EXPIRATION_TIME);
    }

    public void setTokenToCookie(String atValue, String rtValue, HttpServletResponse response) {

        response.addHeader("Set-Cookie"
                , createCookie(
                        JwtProperties.ACCESS_HEADER_STRING
                        , atValue
                        , JwtProperties.TOKEN_COOKIE_MAX_AGE
                ));

        response.addHeader("Set-Cookie"
                , createCookie(
                        JwtProperties.REFRESH_HEADER_STRING
                        , rtValue
                        , JwtProperties.TOKEN_COOKIE_MAX_AGE
                ));
    }

    public void setInoToCookie(String ino, HttpServletResponse response) {
        response.addHeader("Set-Cookie"
                , createCookie(
                        JwtProperties.INO_HEADER_STRING
                        , ino
                        , JwtProperties.INO_COOKIE_MAX_AGE
                ));
    }

    public String createCookie(String name, String value, long expires){
        return ResponseCookie
                .from(name, value)
                .path("/")
                .maxAge(expires)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .build()
                .toString();
    }

    public void deleteToken(String userId, String inoValue, HttpServletResponse response) {
        deleteTokenData(userId, inoValue);
        deleteTokenCookie(response);
    }

    public void deleteTokenData(String userId, String inoValue) {
        String accessTokenKey = JwtProperties.ACCESS_TOKEN_PREFIX + inoValue + userId;
        String refreshTokenKey = JwtProperties.REFRESH_TOKEN_PREFIX + inoValue + userId;

        redisTemplate.delete(accessTokenKey);
        redisTemplate.delete(refreshTokenKey);
    }

    public void deleteTokenCookie(HttpServletResponse response) {
        String[] cookie_arr = {
                JwtProperties.ACCESS_HEADER_STRING
                , JwtProperties.REFRESH_HEADER_STRING
                , JwtProperties.INO_HEADER_STRING
        };

        Arrays.stream(cookie_arr).forEach(name -> {
            Cookie cookie = new Cookie(name, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        });
    }


















    /*


    public boolean checkAccessToken(String accessTokenKey, String accessTokenValue){
        long keyExpire = redisTemplate.getExpire(accessTokenKey);

        log.info("AccessToken keyExpire : {}", keyExpire);

        // 클라이언트는 AccessToken의 만료시간이 1분 적기 때문에
        // AccessToken이 존재하는데 keyExpire가 -2로 삭제된 데이터이거나 60보다 작아 1분 미만으로 만료기간이 남을 수 없기 때문에
        // 탈취된 토큰으로 판단할 수 있다.
        if(keyExpire == -2 || keyExpire < 60) {
            log.info("AccessToekn Expire return false");
            return false;
        }else {
            log.info("AccessToken Expire return true");
            String redisAccessTokenValue = redisTemplate.opsForValue().get(accessTokenKey);

            if(redisAccessTokenValue == null || !redisAccessTokenValue.equals(accessTokenValue))


            return true;
        }
    }
















    //old method

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


        *//*
            토큰 상태가 정상이고 ino가 존재한다면
            1. verify를 통해 claim인 userId를 꺼내고
            2. "rt" + tno + userId의 value값과 refreshTokenVal을 비교한다.
            3. 둘이 일치한다면
                3-1. "at" + tno + userId가 존재하는지 여부를 체크
                    3-1-1. 존재한다면 expire 리턴값이 60보다 큰 경우에는 rt, at 토큰 삭제 후 null 리턴

                3-2. at가 존재하지 않거나 존재하면서 expire 리턴값이 60보다 작은 경우에는 map 데이터를 리턴
            4. 일치하지 않는다면 해당 키값의 토큰을 모두 삭제

         *//*

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



    public boolean checkRefreshToken(String rtKey, String tokenValue){
        ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();
        String value = stringValueOperations.get(rtKey);

        if(value != null && value.equals(tokenValue))
            return true;
        else
            return false;
    }


    //reissuance AccessToken & RefreshToken
    public JwtDTO reIssuanceAllToken(Map<String, String> reIssuedData, HttpServletResponse response){
        *//**
         * @Param
         * reIssuedData = {
         *                  userId : value,
         *                  ino : value
         *               }
         *//*

        log.info("reIssuanceAllToken");

        String userId = reIssuedData.get("userId");
        String ino = reIssuedData.get("ino");

        String atValue = issuedAccessToken(userId, ino);
        String rtValue = issuedRefreshToken(userId, ino);

        setTokenToCookie(atValue, rtValue, ino, response);

        return buildJwtDTO(atValue, rtValue, ino);
    }



    public String loginProcIssuedAllToken(String userId, HttpServletRequest request, HttpServletResponse response){

        Cookie inoCookie = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);
        String ino = null;

        *//*
            로그인 요청이 발생.
            if(ino == null)
                최초 로그인 디바이스로 tno 생성과 각 토큰을 생성.
            else if(ino != null)
                if(checkToken)
                    해당 토큰이 존재하지 않기 때문에 토큰 생성 후 로그인 처리
                else
                    해당 토큰이 존재하기 때문에 토큰 데이터 삭제 tno를 재생성하고 토큰 생성 및 로그인 처리

         *//*

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

//        return buildJwtDTO(userId, ino);

        String atValue = issuedAccessToken(userId, ino);
        String rtValue = issuedRefreshToken(userId, ino);

        return setTokenToCookie(atValue, rtValue, ino, response);
    }





    public JwtDTO buildJwtDTO(String atValue, String rtValue, String ino) {

        return JwtDTO.builder()
                    .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                    .accessTokenValue(JwtProperties.TOKEN_PREFIX + atValue)
                    .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                    .refreshTokenValue(JwtProperties.TOKEN_PREFIX + rtValue)
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



    //issued AccessToken
    public String issuedAccessToken(String userId, String ino){
        String accessToken = JWT.create()
                .withSubject("cocoToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
        stringStringValueOperations.set(JwtProperties.ACCESS_TOKEN_PREFIX + ino + userId, accessToken, Duration.ofHours(2L));

        return accessToken;
    }


    //issued RefreshToken
    public String issuedRefreshToken(String userId, String ino){
        String refreshToken = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
        stringStringValueOperations.set(JwtProperties.REFRESH_TOKEN_PREFIX + ino + userId, refreshToken, Duration.ofDays(14L));

        return refreshToken;
    }*/

}
