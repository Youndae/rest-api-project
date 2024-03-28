package com.example.boardrest.config;


import com.example.boardrest.config.jwt.JwtAuthorizationFilter;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .addFilter(corsFilter)
                .formLogin().disable()
                .httpBasic().disable()
                .logout()
            .and()
                .addFilterBefore(new JwtAuthorizationFilter(memberRepository, jwtTokenProvider), BasicAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "/resources/**")
                .permitAll();

        return http.build();
    }
}
