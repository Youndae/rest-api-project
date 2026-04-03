package com.example.boardrest.mapper;

import com.example.boardrest.domain.dto.member.in.JoinRequest;
import com.example.boardrest.domain.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "auths", ignore = true)
    @Mapping(target = "provider", constant = "local")
    @Mapping(target = "username", source = "userName")
    Member toEntity(JoinRequest dto);

    default Member toFullEntity(JoinRequest dto, BCryptPasswordEncoder passwordEncoder) {
        Member member = toEntity(dto);

        member.updatePassword(dto.getPassword(), passwordEncoder);
        member.addAuth();

        return member;
    }
}
