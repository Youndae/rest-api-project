package com.example.boardrest.config;


import com.example.boardrest.auth.filter.JwtAuthorizationFilter;
import com.example.boardrest.auth.filter.LoginFilter;
import com.example.boardrest.auth.handler.LoginFailureHandler;
import com.example.boardrest.auth.handler.LoginSuccessHandler;
import com.example.boardrest.auth.oAuth.CustomOAuth2UserService;
import com.example.boardrest.auth.oAuth.CustomOAuthSuccessHandler;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.properties.CookieProperties;
import com.example.boardrest.properties.TokenProperties;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.service.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomOAuthSuccessHandler customOAuthSuccessHandler;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final LoginSuccessHandler loginSuccessHandler;

    private final LoginFailureHandler loginFailureHandler;

    private final ObjectMapper om;

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenProperties tokenProperties;

    private final MemberRepository memberRepository;

    private final CookieProperties cookieProperties;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

//    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter filter = new LoginFilter(om);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        filter.setFilterProcessesUrl("/api/member/login");
        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        filter.setAuthenticationFailureHandler(loginFailureHandler);

        return filter;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        JwtAuthorizationFilter jwtAuthFilter = new JwtAuthorizationFilter(
                memberRepository,
                jwtTokenProvider,
                tokenProperties,
                cookieProperties
        );

        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .addFilter(corsFilter)
                .formLogin().disable()
                .httpBasic().disable()
                .logout()
            .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, ErrorCode.FORBIDDEN.getMessage());
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, ErrorCode.FORBIDDEN.getMessage());
                })
            .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "/resources/**", "/api/member/login")
                .permitAll()
            .and()
                .oauth2Login((oauth2) ->
                        oauth2
                                .userInfoEndpoint((userInfoEndpointConfig) ->
                                        userInfoEndpointConfig
                                                .userService(customOAuth2UserService))
                                .successHandler(customOAuthSuccessHandler));

        return http.build();
    }
}
