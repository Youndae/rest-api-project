package com.example.boardrest.service;

import com.example.boardrest.customException.CustomAccessDeniedException;
import com.example.boardrest.customException.CustomAuthenticationException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.member.in.JoinRequest;
import com.example.boardrest.domain.dto.member.in.OAuthJoinRequest;
import com.example.boardrest.domain.dto.member.in.UpdateProfileRequest;
import com.example.boardrest.domain.dto.member.out.ProfileResponse;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.MemberCheckResult;
import com.example.boardrest.mapper.MemberMapper;
import com.example.boardrest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final ImageFileService imageFileService;

    private final MemberMapper memberMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    @Value("#{filePath['file.profile.path']}")
    private String profilePath;

    /*@Override
    public String memberLogin(LoginRequest member, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getUserId(), member.getPassword());
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
    }*/

    /*@Override
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

    }*/

    // 사용자 회원가입(체크 후 메소드 호출로 save 처리)
    @Override
    @Transactional
    public String register(JoinRequest joinRequest) {
        Member member = memberMapper.toFullEntity(joinRequest, passwordEncoder);
        String imageName = null;

        try {
            String profile = null;

            if(joinRequest.hasProfile()){
                profile = imageFileService.profileImageSave(joinRequest.getProfile());
                imageName = profile;
            }

            member.updateProfile(profile);
            memberRepository.save(member);

            memberRepository.flush();

            return "success";
        }catch(Exception e) {
            if(imageName != null)
                imageFileService.deleteFile(profilePath, imageName);

            throw new IllegalArgumentException("register Error: ", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void oAuthJoin(OAuthJoinRequest request, String userId) {
        Member member = memberRepository.findOAuthUserByUserId(userId);

        if(member == null) {
            log.warn("MemberService.oAuthJoin :: join oauth member is null. userId={}", userId);
            throw new CustomAccessDeniedException(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.getMessage());
        }

        member.updateNickname(request.getNickname());
        String imageName = null;

        try {
            String profile = null;

            if(request.hasProfile()){
                profile = imageFileService.profileImageSave(request.getProfile());
                imageName = profile;
            }

            member.updateProfile(profile);
        }catch(Exception e) {
            if(imageName != null)
                imageFileService.deleteFile(profilePath, imageName);

            throw new IllegalArgumentException("register Error: ", e);
        }
    }

    @Override
    public MemberCheckResult checkId(String userId) {
        Member member = memberRepository.findByUserId(userId);

        return member == null ? MemberCheckResult.VALID : MemberCheckResult.DUPLICATED;
    }

    @Override
    public MemberCheckResult checkNickname(String nickname, Principal principal) {
        Member member = memberRepository.findByNickname(nickname);

        MemberCheckResult result;

        if(member == null)
            result = MemberCheckResult.VALID;
        else {
            if(principal != null && member.getUserId().equals(principal.getName()))
                result = MemberCheckResult.VALID;

            result = MemberCheckResult.DUPLICATED;
        }

        return result;
    }

    @Override
    public void updateProfile(UpdateProfileRequest updateProfileRequest, Principal principal) {

        if(principal == null)
            throw new CustomAccessDeniedException(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.getMessage());

        Member member = memberRepository.findByUserId(principal.getName());
        String imageName = null;
        try {
            String profileThumbnail = null;
            if(updateProfileRequest.hasProfile()) {
                profileThumbnail = imageFileService.profileImageSave(updateProfileRequest.getProfile());
                imageName = profileThumbnail;
            }

            if(!updateProfileRequest.hasProfile() && !updateProfileRequest.hasDeleteProfile())
                profileThumbnail = member.getProfile();
            else
                if (updateProfileRequest.hasDeleteProfile())
                    imageFileService.deleteFile(profilePath, updateProfileRequest.getDeleteProfile());

            member.updateProfileData(profileThumbnail, updateProfileRequest.getNickname(), updateProfileRequest.getEmail());

            memberRepository.save(member);
        }catch (Exception e) {
            if(imageName != null)
                imageFileService.deleteFile(profilePath, imageName);

            throw new IllegalArgumentException("Modify profile error");
        }
    }

    @Override
    public ProfileResponse getProfile(Principal principal) {
        ProfileResponse result = memberRepository.getMemberProfileDataByUserId(principal.getName());

        if(result == null){
            log.error("getProfile member is null. userId: {}", principal.getName());
            throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
        }

        return result;
    }
}
