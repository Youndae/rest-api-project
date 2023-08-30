package com.example.boardrest.service;

import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.dto.MemberDTO;
import com.example.boardrest.domain.entity.Auth;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.domain.entity.RefreshToken;
import com.example.boardrest.repository.AuthRepository;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.repository.RefreshTokenRepository;
import com.example.boardrest.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
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

    private final RefreshTokenRepository refreshTokenRepository;

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
    private int joinMember(MemberDTO member){
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
    public JwtDTO memberLogin(Member member) {

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
            String accessToken = tokenProvider.issuedAccessToken(uid);

            log.info("service token : " + accessToken);

            String refreshToken = tokenProvider.issuedRefreshToken(uid);

            return JwtDTO.builder()
                    .accessTokenHeader(JwtProperties.ACCESS_HEADER_STRING)
                    .accessTokenValue(JwtProperties.TOKEN_PREFIX + accessToken)
                    .refreshTokenHeader(JwtProperties.REFRESH_HEADER_STRING)
                    .refreshTokenValue(JwtProperties.TOKEN_PREFIX + refreshToken)
                    .build();
        }

        return null;
    }

    @Override
    public int logout(HttpServletRequest request, Principal principal) {

        try{
            Cookie cookie = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);

            log.info("cookie name : {}", cookie.getName());
            log.info("cookie value : {}", cookie.getValue());

            Map<String, String> verifyRefreshToken = tokenProvider.verifyRefreshToken(request);

            RefreshToken refreshToken = RefreshToken.builder()
                    .userId(principal.getName())
                    .tokenVal(verifyRefreshToken.get("refreshTokenValue"))
                    .rtIndex(verifyRefreshToken.get("rIndex"))
                    .build();

            refreshTokenRepository.delete(refreshToken);

            return 1;
        }catch (Exception e){
            new Exception();
            return 0;
        }

    }
}
