package com.example.boardrest.service;

import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.dto.MemberDTO;
import com.example.boardrest.domain.entity.Auth;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.repository.AuthRepository;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final AuthRepository authRepository;

    // 사용자 회원가입(체크 후 메소드 호출로 save 처리)
    @Override
    public int memberJoinProc(MemberDTO dto) {
        if(dto.getUserId() == null || dto.getUserPw().length() == 0 || dto.getUserName() == null)
            return 0;
        else
            return joinMember(dto);
    }

    // 사용자 데이터 save
    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public int joinMember(MemberDTO member){
        Member memberEntity = Member.builder()
                .userId(member.getUserId())
                .userPw(passwordEncoder.encode(member.getUserPw()))
                .userName(member.getUserName())
                .build();

        memberRepository.save(memberEntity);
        authRepository.save(Auth.builder()
                .userId(member.getUserId())
                .auth("ROLE_MEMBER")
                .build());
        log.info("join success");
        return 1;
    }

    @Override
    public JwtDTO memberLogin(Member member, HttpServletRequest request) {

        log.info("member login service");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getUserId(), member.getUserPw());

        log.info("authenticationToken : " + authenticationToken);

        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        log.info("authentication : " + authentication);

        CustomUser customUser = (CustomUser) authentication.getPrincipal();

        log.info("login service success");

        String uid = customUser.getMember().getUserId();

        if(uid != null){
            //Redis 사용 이전 처리 코드
//            String accessToken = tokenProvider.issuedAccessToken(uid);

//            log.info("service token : " + accessToken);

//            String refreshToken = tokenProvider.issuedRefreshToken(uid);



            /*return JwtDTO.builder()
                    .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                    .accessTokenValue(JwtProperties.TOKEN_PREFIX + accessToken)
                    .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                    .refreshTokenValue(JwtProperties.TOKEN_PREFIX + refreshToken)
                    .build();*/

            return tokenProvider.loginProcIssuedAllToken(uid, request);
        }

        return null;
    }

    @Override
    public int logout(HttpServletRequest request, Principal principal) {

        try{
            Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

            Map<String, String> verifyRefreshToken = tokenProvider.verifyRefreshToken(request);

            String userId = verifyRefreshToken.get("userId");

            tokenProvider.deleteTokenData(ino.getValue(), userId);

            return 1;
        }catch (Exception e){
            new Exception();
            return 0;
        }

    }
}
