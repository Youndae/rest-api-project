package com.example.boardrest.service;

import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.dto.MemberDTO;
import com.example.boardrest.domain.entity.Member;
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

import javax.transaction.Transactional;

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
    @Transactional(rollbackOn = Exception.class)
    public int memberJoinProc(MemberDTO dto) {
        if(dto.getUserId() == null || dto.getUserPw().length() == 0 || dto.getUserName() == null)
            return 0;
        else
            return joinMember(dto);
    }

    // 사용자 데이터 save
    private int joinMember(MemberDTO member){
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
