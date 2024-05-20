package com.example.boardrest.service;

import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.customException.CustomAccessDeniedException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.JoinDTO;
import com.example.boardrest.domain.dto.ProfileDTO;
import com.example.boardrest.domain.dto.ProfileResponseDTO;
import com.example.boardrest.domain.entity.Auth;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.OAuthProvider;
import com.example.boardrest.domain.enumuration.Role;
import com.example.boardrest.properties.FilePathProperties;
import com.example.boardrest.repository.AuthRepository;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final JwtTokenProvider tokenProvider;

    private final AuthRepository authRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final ImageFileService imageFileService;

    // 사용자 회원가입(체크 후 메소드 호출로 save 처리)
    @Override
    public int memberJoinProc(JoinDTO dto, MultipartFile profileThumbnail) {
        if(dto.getUserId() == null || dto.getUserPw().length() == 0 || dto.getUserName() == null || dto.getNickname() == null)
            return 0;
        else
            return joinMember(dto, profileThumbnail);
    }

    // 사용자 데이터 save
    @Transactional(rollbackOn = Exception.class)
    public int joinMember(JoinDTO member, MultipartFile profileThumbnail){

        String filepath = FilePathProperties.PROFILE_FILE_PATH;

        String profile = profileThumbnail == null ?
                    null : imageFileService.saveFile(filepath, profileThumbnail).get("imageName");

        Member memberEntity = Member.builder()
                            .userId(member.getUserId())
                            .userPw(member.getUserPw())
                            .username(member.getUserName())
                            .email(member.getEmail())
                            .nickname(member.getNickname())
                            .profileThumbnail(profile)
                            .provider(OAuthProvider.LOCAL.getKey())
                            .build();

        memberEntity.addAuth(
                Auth.builder()
                        .auth(Role.MEMBER.getKey())
                        .build()
        );

        memberRepository.save(memberEntity);

        return 1;
    }

    @Override
    public Long memberLogin(Member member, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getUserId(), member.getUserPw());
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String uid = customUser.getMember().getUserId();

        if(uid != null) {
            Cookie inoCookie = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

            if(inoCookie == null)
                tokenProvider.issuedAllToken(uid, response);
            else
                tokenProvider.issuedToken(uid, inoCookie.getValue(), response);

            return 1L;
        }

        throw new BadCredentialsException("login Fail");
    }

    @Override
    public int logout(HttpServletRequest request, HttpServletResponse response, Principal principal) {

        try{
            String inoValue = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING).getValue();
            String userId = principal.getName();

            tokenProvider.deleteToken(userId, inoValue, response);

            return 1;
        }catch (Exception e){
            log.info("logout Exception : {}", e.getMessage());
            return 0;
        }

    }

    @Override
    public Long modifyProfile(ProfileDTO profileDTO, Principal principal) {

        if(principal == null)
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        String filepath = FilePathProperties.PROFILE_FILE_PATH;

        Member member = memberRepository.findByUserId(principal.getName());
        String profileThumbnail = profileDTO.getProfileThumbnail() == null ?
                                    null : imageFileService.saveFile(filepath, profileDTO.getProfileThumbnail()).get("imageName");

        if(profileDTO.getProfileThumbnail() == null && profileDTO.getDeleteProfile() == null) {
            profileThumbnail = member.getProfileThumbnail();
        }else {
            if (profileDTO.getDeleteProfile() != null) {
                imageFileService.deleteFile(filepath, profileDTO.getDeleteProfile());
            }
        }

        member.setNickName(profileDTO.getNickname());
        member.setProfileThumbnail(profileThumbnail);

        memberRepository.save(member);

        return 1L;
    }

    @Override
    public ProfileResponseDTO getProfile(Principal principal) {

        Member member = memberRepository.findByUserId(principal.getName());

        return ProfileResponseDTO.builder()
                .nickname(member.getNickname())
                .profileThumbnail(member.getProfileThumbnail())
                .build();
    }
}
