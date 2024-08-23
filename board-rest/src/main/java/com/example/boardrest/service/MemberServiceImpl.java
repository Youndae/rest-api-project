package com.example.boardrest.service;

import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.customException.CustomAccessDeniedException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.member.in.JoinDTO;
import com.example.boardrest.domain.dto.member.in.ProfileDTO;
import com.example.boardrest.domain.dto.member.out.ProfileResponseDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.Result;
import com.example.boardrest.properties.FilePathProperties;
import com.example.boardrest.repository.AuthRepository;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.auth.user.CustomUser;
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
import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final JwtTokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final ImageFileService imageFileService;

    // 사용자 회원가입(체크 후 메소드 호출로 save 처리)
    @Override
    public String memberJoinProc(JoinDTO dto, MultipartFile profileThumbnail) {
        if(dto.getUserId() == null || dto.getUserPw().length() == 0 || dto.getUserName() == null || dto.getNickname() == null)
            return Result.FAIL.getResultMessage();
        else
            return joinMember(dto, profileThumbnail);
    }

    @Override
    public String checkId(String userId) {
        Member member = memberRepository.findByUserId(userId);

        return member == null ? Result.AVAILABLE.getResultMessage() : Result.DUPLICATED.getResultMessage();
    }

    @Override
    public String checkNickname(String nickname, Principal principal) {
        Member member = memberRepository.findByNickname(nickname);

        String result;

        if(member == null)
            result = Result.AVAILABLE.getResultMessage();
        else {
            if(principal != null && member.getUserId().equals(principal.getName()))
                result = Result.AVAILABLE.getResultMessage();

            result = Result.DUPLICATED.getResultMessage();
        }

        return result;
    }

    // 사용자 데이터 save
    @Transactional(rollbackOn = Exception.class)
    public String joinMember(JoinDTO member, MultipartFile profileThumbnail){

        String filepath = FilePathProperties.PROFILE_FILE_PATH;
        String imageName = null;

        try {
            String profile = null;

            if(profileThumbnail != null){
                profile = imageFileService.saveFile(filepath, profileThumbnail).get("imageName");
                imageName = profile;
            }

            Member memberEntity = member.toEntity(profile);

            memberEntity.addAuth();

            memberRepository.save(memberEntity);
        }catch (Exception e) {
            if(imageName != null)
                imageFileService.deleteFile(filepath, imageName);

            throw new IllegalArgumentException("Modify profile error");
        }



        return Result.SUCCESS.getResultMessage();
    }

    @Override
    public String memberLogin(Member member, HttpServletRequest request, HttpServletResponse response) {
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

            return Result.SUCCESS.getResultMessage();
        }

        throw new BadCredentialsException("login Fail");
    }

    @Override
    public String logout(HttpServletRequest request, HttpServletResponse response, Principal principal) {

        try{
            String inoValue = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING).getValue();
            String userId = principal.getName();

            tokenProvider.deleteToken(userId, inoValue, response);

            return Result.SUCCESS.getResultMessage();
        }catch (Exception e){
            log.info("logout Exception : {}", e.getMessage());
            return Result.FAIL.getResultMessage();
        }

    }

    @Override
    public String modifyProfile(ProfileDTO profileDTO, Principal principal) {

        if(principal == null)
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        String filepath = FilePathProperties.PROFILE_FILE_PATH;

        Member member = memberRepository.findByUserId(principal.getName());
        String imageName = null;
        try {
            String profileThumbnail = null;
            if(profileDTO.getProfileThumbnail() != null) {
                profileThumbnail = imageFileService.saveFile(filepath, profileDTO.getProfileThumbnail()).get("imageName");
                imageName = profileThumbnail;
            }

            if(profileDTO.getProfileThumbnail() == null && profileDTO.getDeleteProfile() == null)
                profileThumbnail = member.getProfileThumbnail();
            else
                if (profileDTO.getDeleteProfile() != null)
                    imageFileService.deleteFile(filepath, profileDTO.getDeleteProfile());

            member.setNickName(profileDTO.getNickname());
            member.setProfileThumbnail(profileThumbnail);

            memberRepository.save(member);
        }catch (Exception e) {
            if(imageName != null)
                imageFileService.deleteFile(filepath, imageName);

            throw new IllegalArgumentException("Modify profile error");
        }

        return Result.SUCCESS.getResultMessage();
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
