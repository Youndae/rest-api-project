package com.example.boardrest.security.domain;

import com.example.boardrest.domain.entity.Member;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class CustomUser extends User {

    private Member member;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities){
        super(username, password, authorities);
    }

    public CustomUser(Member member){
        super(member.getUserId(), member.getUserPw(), member.getAuths().stream().map(auth ->
                new SimpleGrantedAuthority(auth.getAuth())).collect(Collectors.toList()));

        log.info("customUser");
        log.info("customUser member : {}", member);
        log.info("auth stream : {}", member.getAuths().stream().map(auth ->
            new SimpleGrantedAuthority(auth.getAuth())
        ).collect(Collectors.toList()));
        log.info("customUser auths : {}", member.getAuths());

        this.member = member;
    }
}
