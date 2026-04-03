package com.example.boardrest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.boardrest.domain.enumuration.TokenValidationResult;
import com.example.boardrest.properties.CookieProperties;
import com.example.boardrest.properties.JwtSecretProperties;
import com.example.boardrest.properties.TokenProperties;
import com.example.boardrest.properties.TokenRedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final TokenProperties tokenProperties;

    private final TokenRedisProperties tokenRedisProperties;

    private final CookieProperties cookieProperties;

    private final JwtSecretProperties jwtSecretProperties;

    private final StringRedisTemplate redisTemplate;

    public String decodeToken(Cookie tokenCookie) {
        String tokenValue = tokenCookie.getValue().replace(tokenProperties.getPrefix(), "");

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
        String accessTokenValue = getTokenCookieValue(accessToken);
        String accessClaimByUserId = getClaimUserIdByToken(accessTokenValue, jwtSecretProperties.getAccess());

        //잘못된 토큰인 경우 null이 반환될 것이고,
        //만료된 토큰이라면 Exception이 발생해 예외처리로 인해 TOKEN_EXPIRATION_RESULT가 반환된다.
        if (accessClaimByUserId == null || accessClaimByUserId.equals(TokenValidationResult.WRONG_TOKEN.getResult()))
            return TokenValidationResult.WRONG_TOKEN.getResult();
        else if(accessClaimByUserId.equals(TokenValidationResult.TOKEN_EXPIRATION.getResult()))
            return TokenValidationResult.TOKEN_EXPIRATION.getResult();


        String redisValue = getTokenValueData(tokenRedisProperties.getAccess().getPrefix(), inoValue, accessClaimByUserId);

        if(accessTokenValue.equals(redisValue))
            return accessClaimByUserId;
        else {
            deleteTokenData(accessClaimByUserId, inoValue);
            return TokenValidationResult.TOKEN_STEALING.getResult();
        }
    }

    public String verifyRefreshToken(Cookie refreshToken, String inoValue, String accessTokenClaim) {
         String refreshTokenValue = getTokenCookieValue(refreshToken);
         String refreshClaimByUserId = getClaimUserIdByToken(refreshTokenValue, jwtSecretProperties.getRefresh());

         if(refreshClaimByUserId == null || refreshClaimByUserId.equals(TokenValidationResult.WRONG_TOKEN.getResult()))
             return TokenValidationResult.WRONG_TOKEN.getResult();
         else if(refreshClaimByUserId.equals(TokenValidationResult.TOKEN_EXPIRATION.getResult()))
             return TokenValidationResult.TOKEN_EXPIRATION.getResult();
         else if(!refreshClaimByUserId.equals(accessTokenClaim)){
             deleteTokenData(refreshClaimByUserId, inoValue);
             return TokenValidationResult.TOKEN_STEALING.getResult();
         }

         String redisValue = getTokenValueData(tokenRedisProperties.getRefresh().getPrefix(), inoValue, refreshClaimByUserId);

         if(refreshTokenValue.equals(redisValue))
             return refreshClaimByUserId;
         else {
             deleteTokenData(refreshClaimByUserId, inoValue);
             return TokenValidationResult.TOKEN_STEALING.getResult();
         }
    }

    private String getTokenCookieValue(Cookie token) {
        return token.getValue().replace(tokenProperties.getPrefix(), "");
    }

    public String getTokenValueData(String tokenPrefix, String inoValue, String claim) {
        String tokenKey = tokenPrefix + inoValue + claim;
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
            return JWT.require(Algorithm.HMAC512(secret))
                        .build()
                        .verify(tokenValue)
                        .getClaim("userId")
                        .asString();
        }catch(TokenExpiredException e) {
            //토큰 만료 exception
            return TokenValidationResult.TOKEN_EXPIRATION.getResult();
        }catch (JWTDecodeException e) {
            //비정상 토큰. 검증할 수 없는 토큰.
            return TokenValidationResult.WRONG_TOKEN.getResult();
        }
    }

    //ino를 제외한 accessToken, refreshToken 생성
    public void issuedToken(String userId, String inoValue, HttpServletResponse response) {
        String accessToken = issuedAccessToken(userId, inoValue);
        String refreshToken = issuedRefreshToken(userId, inoValue);
        Duration accessExpiration = getAccessTokenRedisAndCookieDuration();
        Duration refreshExpiration = getRefreshTokenRedisAndCookieDuration();

        setCookie(tokenProperties.getAccess().getHeader(), accessToken, accessExpiration, response);
        setCookie(tokenProperties.getRefresh().getHeader(), refreshToken, refreshExpiration, response);
    }

    //ino까지 모두 생성
    public void issuedAllToken(String userId, HttpServletResponse response) {
        String inoValue = issuedIno();
        issuedToken(userId, inoValue, response);

        setCookie(cookieProperties.getIno().getHeader(), inoValue, Duration.ofDays(cookieProperties.getIno().getAge()), response);
    }

    public String issuedAccessToken(String userId, String inoValue) {
        String accessToken = createToken(userId, jwtSecretProperties.getAccess(), tokenProperties.getAccess().getExpiration());
        Duration accessExpiration = getAccessTokenRedisAndCookieDuration();
        setRedisByToken(tokenRedisProperties.getAccess().getPrefix(), inoValue, userId, accessToken, accessExpiration);

        return tokenProperties.getPrefix() + accessToken;
    }

    public String issuedRefreshToken(String userId, String inoValue) {
        String refreshToken = createToken(userId, jwtSecretProperties.getRefresh(), tokenProperties.getRefresh().getExpiration());
        Duration refreshExpiration = getRefreshTokenRedisAndCookieDuration();
        setRedisByToken(tokenRedisProperties.getRefresh().getPrefix(), inoValue, userId, refreshToken, refreshExpiration);

        return tokenProperties.getPrefix() + refreshToken;
    }

    private Duration getAccessTokenRedisAndCookieDuration() {
        return Duration.ofHours(tokenRedisProperties.getAccess().getExpiration());
    }

    private Duration getRefreshTokenRedisAndCookieDuration() {
        return Duration.ofHours(tokenRedisProperties.getRefresh().getExpiration());
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

    public void setRedisByToken(String tokenPrefix, String ino, String claim, String value, Duration expiration) {
        String key = tokenPrefix + ino + claim;
        ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();
        stringValueOperations.set(key, value, expiration);
    }

    public void setCookie(String header, String value, Duration expires, HttpServletResponse response) {

        response.addHeader("Set-Cookie",
                createCookie(
                        header,
                        value,
                        expires
                )
        );
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
        String accessTokenKey =  tokenRedisProperties.getAccess().getPrefix() + inoValue + userId;
        String refreshTokenKey = tokenRedisProperties.getRefresh().getPrefix() + inoValue + userId;

        redisTemplate.delete(accessTokenKey);
        redisTemplate.delete(refreshTokenKey);
    }

    public void deleteTokenCookie(HttpServletResponse response) {
        String[] cookie_arr = {
                tokenProperties.getAccess().getHeader(),
                tokenProperties.getRefresh().getHeader(),
                cookieProperties.getIno().getHeader()
        };

        Arrays.stream(cookie_arr).forEach(name -> {
            Cookie cookie = new Cookie(name, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        });
    }
}
