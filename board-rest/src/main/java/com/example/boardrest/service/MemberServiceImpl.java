package com.example.boardrest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.Member;
import com.example.boardrest.domain.RefreshToken;
import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.repository.RefreshTokenRepository;
import com.example.boardrest.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final BCryptPasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtTokenProvider tokenProvider;

    // 사용자 회원가입(체크 후 메소드 호출로 save 처리)
    @Override
    public int memberJoinProc(Member member) {
        if(member.getUserId() == null || member.getUserPw().length() == 0 || member.getUserName() == null)
            return 0;
        else
            return joinMember(member);
    }

    // 사용자 데이터 save
    private int joinMember(Member member){
        try{
            Member memberEntity = Member.builder()
                    .userId(member.getUserId())
                    .userPw(passwordEncoder.encode(member.getUserPw()))
                    .userName(member.getUserName())
                    .build();

            memberRepository.save(memberEntity);
            log.info("join success");
            return 1;
        }catch (Exception e){
            log.info("failed Join");
            return 0;
        }
    }

    /*@Override
    public JwtDTO memberLogin(Member member) {

        log.info("member login service");

        try{
            log.info("userId : " + member.getUserId());
            log.info("userPw : " + member.getUserPw());


            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(member.getUserId(), member.getUserPw());

            log.info("authenticationToken : " + authenticationToken);

            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);

            log.info("authentication : " + authentication);

            CustomUser customUser = (CustomUser) authentication.getPrincipal();

            log.info("login service success");

            String uid = customUser.getMember().getUserId();

            String accessToken = JWT.create()
                    .withSubject("cocoToken")
                    .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_EXPIRATION_TIME))
                    .withClaim("userId", uid)
                    .sign(Algorithm.HMAC512(JwtProperties.SECRET));

            log.info("service token : " + accessToken);

            *//**
             * refreshTokenrepository로 save를 먼저 해주고.
             * 그럼 난수 + userId로 index를 설정한 뒤
             * refreshToken 생성 후 tokenvalue와 Expires를 추가?
             *
             * 아니면 refreshToken 생성 후 난수 + userId로 index
             * value 및 Expires save?
             *
             *//*

            Date refreshExpires = new Date(System.currentTimeMillis() + JwtProperties.REFRESH_EXPIRATION_TIME);

            String refreshToken = JWT.create()
                    .withExpiresAt(refreshExpires)
                    .withClaim("userId", uid)
                    .sign(Algorithm.HMAC512(JwtProperties.SECRET));

            StringBuffer sb = new StringBuffer();

            String rIndex = sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()))
                            .append(UUID.randomUUID().toString()).toString();

            refreshTokenRepository.save(RefreshToken.builder()
                                            .rtIndex(rIndex)
                                            .userId(uid)
                                            .tokenVal(refreshToken)
                                            .build()
                                        );

            JwtDTO dto = JwtDTO.builder()
                    .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                    .accessTokenValue(JwtProperties.TOKEN_PREFIX + accessToken)
                    .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                    .refreshTokenValue(JwtProperties.TOKEN_PREFIX + refreshToken)
                    .build();

            return dto;
        }catch (Exception e){
            log.info("loginService Exception");
            e.printStackTrace();
        }

        return null;
    }*/

    @Override
    public JwtDTO memberLogin(Member member) {

        log.info("member login service");

        try{
            log.info("userId : " + member.getUserId());
            log.info("userPw : " + member.getUserPw());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(member.getUserId(), member.getUserPw());

            log.info("authenticationToken : " + authenticationToken);

            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);

            log.info("authentication : " + authentication);

            CustomUser customUser = (CustomUser) authentication.getPrincipal();

            log.info("login service success");

            String uid = customUser.getMember().getUserId();

            String accessToken = tokenProvider.issuedAccessToken(uid);

            log.info("service token : " + accessToken);

            String refreshToken = tokenProvider.issuedRefreshToken(uid);

            JwtDTO dto = JwtDTO.builder()
                    .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                    .accessTokenValue(JwtProperties.TOKEN_PREFIX + accessToken)
                    .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                    .refreshTokenValue(JwtProperties.TOKEN_PREFIX + refreshToken)
                    .build();

            return dto;
        }catch (Exception e){
            log.info("loginService Exception");
            e.printStackTrace();
        }

        return null;
    }
}
