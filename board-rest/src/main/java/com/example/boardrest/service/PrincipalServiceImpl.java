package com.example.boardrest.service;

import com.example.boardrest.domain.entity.Member;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class PrincipalServiceImpl implements PrincipalService{

    @Override
    public Member checkPrincipal(Principal principal) {

        try{
            Member member = new Member();
            member.setUserId(principal.getName());
            return member;
        }catch (Exception e){
            return null;
        }

    }
}
