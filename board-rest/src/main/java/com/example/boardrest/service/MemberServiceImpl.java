package com.example.boardrest.service;

import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.dto.MemberDTO;
import com.example.boardrest.domain.entity.Auth;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.repository.AuthRepository;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final JwtTokenProvider tokenProvider;

    private final AuthRepository authRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // 사용자 회원가입(체크 후 메소드 호출로 save 처리)
    @Override
    public int memberJoinProc(MemberDTO dto) {
        if(dto.getUserId() == null || dto.getUserPw().length() == 0 || dto.getUserName() == null)
            return 0;
        else
            return joinMember(dto);
    }

    // 사용자 데이터 save
    @Transactional(rollbackOn = Exception.class)
    public int joinMember(MemberDTO member){
        memberRepository.save(
                                Member.builder()
                                        .userId(member.getUserId())
                                        .userPw(member.getUserPw())
                                        .userName(member.getUserName())
                                        .build()
                        );

        authRepository.save(
                                Auth.builder()
                                .userId(member.getUserId())
                                .auth("ROLE_MEMBER")
                                .build()
                        );

        return 1;
    }

    @Override
    public Long memberLogin(Member member, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getUserId(), member.getUserPw());
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String uid = customUser.getMember().getUserId();

        if(uid != null) {
            Cookie inoCookie = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

            if(inoCookie == null)
                tokenProvider.issuedAllToken(uid, response);
            else
                tokenProvider.issuedToken(uid, inoCookie.getValue(), response);

            return 1L;
        }

        return 0L;
    }

    @Override
    public int logout(HttpServletRequest request, HttpServletResponse response, Principal principal) {

        try{
            String inoValue = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING).getValue();
            String userId = principal.getName();

            tokenProvider.deleteToken(userId, inoValue, response);

            return 1;
        }catch (Exception e){
            log.info("logout Exception : {}", e.getMessage());
            return 0;
        }

    }
}
