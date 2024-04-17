package com.example.boardrest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.boardrest.config.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("#{jwt['token.access.secret']}")
    private String accessSecret;

    @Value("#{jwt['token.access.expiration']}")
    private Long accessTokenExpiration;

    @Value("#{jwt['token.refresh.secret']}")
    private String refreshSecret;

    @Value("#{jwt['token.refresh.expiration']}")
    private Long refreshTokenExpiration;

    @Value("#{jwt['redis.expirationDay']}")
    private Long redisExpirationDay;

    @Value("#{jwt['redis.accessPrefix']}")
    private String redisAccessPrefix;

    @Value("#{jwt['redis.refreshPrefix']}")
    private String redisRefreshPrefix;

    @Value("#{jwt['cookie.tokenAgeDay']}")
    private Long tokenCookieAge;

    @Value("#{jwt['cookie.inoAgeDay']}")
    private Long inoCookieAge;

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
        String accessClaimByUserId = getClaimUserIdByToken(accessTokenValue, accessSecret);

        //잘못된 토큰인 경우 null이 반환될 것이고,
        //만료된 토큰이라면 Exception이 발생해 예외처리로 인해 TOKEN_EXPIRATION_RESULT가 반환된다.
        if (accessClaimByUserId == null)
            return null;
        else if(accessClaimByUserId.equals(JwtProperties.TOKEN_EXPIRATION_RESULT))
            return JwtProperties.TOKEN_EXPIRATION_RESULT;
        else if(accessClaimByUserId.equals(JwtProperties.WRONG_TOKEN))
            return JwtProperties.WRONG_TOKEN;

        String accessTokenKey = redisAccessPrefix + inoValue + accessClaimByUserId;
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
         String refreshClaimByUserId = getClaimUserIdByToken(refreshTokenValue, refreshSecret);

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

         String refreshTokenKey = redisRefreshPrefix + inoValue + refreshClaimByUserId;
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
        String accessToken = createToken(userId, accessSecret, accessTokenExpiration);
        String key = redisAccessPrefix + inoValue + userId;

        setRedisByToken(key, accessToken);

        return JwtProperties.TOKEN_PREFIX + accessToken;
    }

    public String issuedRefreshToken(String userId, String inoValue) {
        String refreshToken = createToken(userId, refreshSecret, refreshTokenExpiration);
        String key = redisRefreshPrefix + inoValue + userId;

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
        stringValueOperations.set(key, value, Duration.ofDays(redisExpirationDay));
    }

    public void setTokenToCookie(String atValue, String rtValue, HttpServletResponse response) {

        response.addHeader("Set-Cookie"
                , createCookie(
                        JwtProperties.ACCESS_HEADER_STRING
                        , atValue
                        , Duration.ofDays(tokenCookieAge)
                ));

        response.addHeader("Set-Cookie"
                , createCookie(
                        JwtProperties.REFRESH_HEADER_STRING
                        , rtValue
                        , Duration.ofDays(tokenCookieAge)
                ));
    }

    public void setInoToCookie(String ino, HttpServletResponse response) {
        response.addHeader("Set-Cookie"
                , createCookie(
                        JwtProperties.INO_HEADER_STRING
                        , ino
                        , Duration.ofDays(inoCookieAge)
                ));
    }

    public String createCookie(String name, String value, Duration expires){
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
        String accessTokenKey = redisAccessPrefix + inoValue + userId;
        String refreshTokenKey = redisRefreshPrefix + inoValue + userId;

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
}
