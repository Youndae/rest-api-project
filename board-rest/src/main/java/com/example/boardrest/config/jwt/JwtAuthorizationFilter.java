package com.example.boardrest.config.jwt;

import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.security.domain.CustomUser;
import com.example.boardrest.service.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider){
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /*@Override
    protected void doFilterInternal(HttpServletRequest request
                                        , HttpServletResponse response
                                        , FilterChain chain)
                                        throws IOException, ServletException {
        log.info("Authorities request");

        Cookie jwtCookie = WebUtils.getCookie(request, JwtProperties.ACCESS_HEADER_STRING);

        *//**
         * 쿠키명은 Authorization과 Authorization_Refresh 두가지가 존재.
         *
         * 그럼 AccessToken만 넘어온건지 RefreshToken만 넘어온건지 확인하기 위해서는
         * 쿠키명을 확인해야하는데 방법이 없는듯.
         * 그럼 두가지 쿠키를 모두 생성해준 뒤
         * 조건문으로 처리하는 방법.
         *
         * 1. Authorization cookie가 존재하는지 null 체크.
         * 2. Authorization cookie가 존재하지 않는다면 Authorization_Refresh cookie가 존재하는지 null체크
         * 3. 이때 두 조건문은 모두 prefix가 TOKEN_PREFIX와 동일한지 같이 체크.
         * 4. Authorization cookie가 존재한다면 기능 처리 계속
         * 5. Authorization cookie가 존재하지 않고 Authorization_Refresh cookie만 존재한다면 Authorization cookie 재발급 후 기능처리
         *
         * 한가지 방법으로 AuthorizationFilter는 이대로 유지하고
         * 클라이언트 서버에서 만료기간을 체크해 AccessToken 혹은 RefreshToken으로 구분해서 보낸다면 아예 RefreshToken을 보낼때는 tokenProvider로
         * 연결해서 보내는 것도 방법이 될듯.
         * 단, 그렇게 하려면 RefreshToken에 무조건 사용자 아이디가 포함이 되어야 함.
         *
         * 만약 AccessToken과 RefreshToken의 쿠키 만료기간을 동일하게 잡는다고 가정.
         * AccessToken에는 userId를 포함하고 있고 RefreshToken에는 tokenValue의 index 값을 갖고 있다고 하면.
         * AccessToken만료시에 RefreshToken으로 재발급을 해야할 때,
         * RefreshToken에 있는 index와 AccessToken에 있는 userId를 같이 합쳐야만 검증할 수 있도록 api 서버에서 처리한다면,
         * RefreshToken만 탈취당한 경우 RefreshToken만 요청으로 들어와 AccessToken이 재발급 되는 경우를 방지할 수 있다.
         *
         * 하지만 단점으로는 AccessToken 자체의 만료기간은 짧지만 쿠키에 존재하는 기간이 길어지게 되므로
         * 만약 userId외에 다른 정보가 추가적으로 들어가야 한다면 오히려 문제가 발생할 수 있다.
         * 하지만 jwtToken에는 기본적으로 민감한 정보는 넣지 않도록 되어있기 때문에 큰 문제는 발생하지 않을것이라는것이 내 생각.
         * 둘다 탈취되면 소용없지 않느냐? 라고 할 수 있지만
         * 둘다 탈취되는것을 감안해 RefreshToken 하나만으로 재발급이 가능하도록 하면 어차피 그게 그거 아닌가?
         * 한가지만 탈취되는 경우 대비할 수 있는 대비책이 하나 더 생기는 것이 오히려 보안에 있어서 이득이라고 생각.
         * 이렇게 처리한다면 refreshToken과 AccessToken을 각각 localStorage, cookie에 나눠서 보관을 해도 될것이라고 생각하고
         * localStorage가 xss 공격에 의해 탈취되더라도 AccessToken을 재발급해주는 경우를 방지할 수 있을것.
         * 애초에 세션방식이 아닌 상황에서 refreshToken 하나만 받았다고 AccessToken이 재발급 되는것은 검증 과정에 있어서 너무 위험하다고 생각하고
         * refreshToken에 어떠한 조치를 취하더라도 탈취당했을 때 보내기만 하면 refreshToken만으로 AccessToken을 재발급 할 수 있을것이라고 생각하기에
         * 재발급에서는 만료된 AccessToken이라도 같이 와야 refreshToken 하나만으로 재발급 처리가 되는것을 막을 수 있지 않을까 생각.
         *//*


        // header에 cookie가 존재하지 않거나 cookie값의 시작이 Bearer로 시작하지 않는 경우 검증하지 않고 넘김
        if(jwtCookie == null || !jwtCookie.getValue().startsWith(JwtProperties.TOKEN_PREFIX)){
            log.info("cookie is null or prefix value not Bearer");
            chain.doFilter(request, response);
            return;
        }

        log.info("cookie is not null");

        String jwtToken = jwtCookie.getValue().replace(JwtProperties.TOKEN_PREFIX, "");

        log.info("AuthorizationFilter token value : " + jwtToken);

        String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build()
                .verify(jwtToken)
                .getClaim("userId")
                .asString();

        log.info("AuthorizationFilter verify username : " + username);

        if(username != null){
            log.info("username is nomal state");

            Member memberEntity = memberRepository.findByUserId(username);

            log.info("AuthorizationFilter memberEntity : " + memberEntity.getUserId());

            CustomUser customUser = new CustomUser(memberEntity);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());


            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }
    }*/

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain chain)
            throws IOException, ServletException {
        log.info("Authorities request");

        Cookie jwtCookie = WebUtils.getCookie(request, JwtProperties.ACCESS_HEADER_STRING);
        Cookie rtCookie = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);

        // header에 cookie가 존재하지 않거나 cookie값의 시작이 Bearer로 시작하지 않는 경우 검증하지 않고 넘김
        if(jwtCookie == null || !jwtCookie.getValue().startsWith(JwtProperties.TOKEN_PREFIX) || rtCookie == null || !rtCookie.getValue().startsWith(JwtProperties.TOKEN_PREFIX)){
            log.info("cookie is null or prefix value not Bearer");
            chain.doFilter(request, response);
            return;
        }

        log.info("cookie is not null");

        String username = jwtTokenProvider.verifyAccessToken(jwtCookie);

        log.info("AuthorizationFilter verify username : " + username);

        if(username != null){
            log.info("username is nomal state");

            Member memberEntity = memberRepository.findByUserId(username);

            log.info("AuthorizationFilter memberEntity : " + memberEntity.getUserId());

            CustomUser customUser = new CustomUser(memberEntity);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }
    }

}
