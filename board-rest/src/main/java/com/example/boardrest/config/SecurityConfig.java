package com.example.boardrest.config;

import com.example.boardrest.config.jwt.JwtAuthenticationFilter;
import com.example.boardrest.config.jwt.JwtAuthorizationFilter;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.security.CustomLogoutSuccessHandler;
import com.example.boardrest.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private AuthenticationManager authenticationManager;

    private final AuthenticationManagerBuilder localConfigureAuthenticationBldr;

    private boolean authenticationManagerInitialized;

    private boolean disableLocalConfigureAuthenticationBldr;

    private final AuthenticationConfiguration authenticationConfiguration;



    /*@Bean
    public AuthenticationSuccessHandler loginSuccessHandler(){
        return new CustomAuthenticationSuccessHandler();
    }*/

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/token/reissued");
    }

    @Bean
    public CustomLogoutSuccessHandler logoutSuccessHandler(){
        return new CustomLogoutSuccessHandler();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                            .addFilter(corsFilter)
                            .formLogin().disable()
                            .httpBasic().disable()
                            .logout().logoutSuccessHandler(logoutSuccessHandler())
                        .and()
                            .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                            .addFilter(new JwtAuthorizationFilter(authenticationManager(), memberRepository, jwtTokenProvider))
                            .authorizeRequests()
                            .antMatchers("/", "/resources/**")
                            .permitAll();

        /*http.authorizeRequests()
                .antMatchers("/", "/login/**", "/resources/**")
                .permitAll()

            .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/imageBoard/imageBoardList")
                .invalidateHttpSession(true)
            .and()
                .exceptionHandling().accessDeniedPage("/");*/

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
        if(!this.authenticationManagerInitialized){
            this.configure(this.localConfigureAuthenticationBldr);
            if(this.disableLocalConfigureAuthenticationBldr){
                this.authenticationManager = this.authenticationConfiguration.getAuthenticationManager();
            }else{
                this.authenticationManager = (AuthenticationManager) this.localConfigureAuthenticationBldr.build();
            }

            this.authenticationManagerInitialized = true;
        }

        return this.authenticationManager;
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        this.disableLocalConfigureAuthenticationBldr = true;
    }

}
