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
        log.info("join success");

        return 1;
    }

    @Override
    public JwtDTO memberLogin(Member member, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getUserId(), member.getUserPw());

        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);
        CustomUser customUser = (CustomUser) authentication.getPrincipal();

        String uid = customUser.getMember().getUserId();

        if(uid != null)
            return tokenProvider.loginProcIssuedAllToken(uid, request);

        return null;
    }

    @Override
    public int logout(HttpServletRequest request, Principal principal) {

        try{
            String inoValue = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING).getValue();
            Map<String, String> verifyRefreshToken = tokenProvider.verifyRefreshToken(request);
            String userId = verifyRefreshToken.get("userId");

            tokenProvider.deleteTokenData(inoValue, userId);

            return 1;
        }catch (Exception e){
            log.info("logout Exception : {}", e.getMessage());
            return 0;
        }

    }
}
