package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.LoginStateDTO;
import com.example.boardrest.domain.dto.JoinDTO;
import com.example.boardrest.domain.dto.ProfileDTO;
import com.example.boardrest.domain.dto.ProfileResponseDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.properties.FilePathProperties;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    @GetMapping("/check-login")
    public ResponseEntity checkLogin(Principal principal) {

        LoginStateDTO responseDTO = new LoginStateDTO();

        if(principal != null)
            responseDTO.setStatusValueForTrue();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PostMapping("/join")
    public int joinProc(@RequestBody JoinDTO dto){

        return memberService.memberJoinProc(dto);
    }

    @GetMapping("/check-id")
    public int checkUserId(@RequestParam("userId") String userId){

        if(memberRepository.findByUserId(userId) != null)
            return 1;
        else
            return 0;
    }

    @GetMapping("/check-nickname")
    public int checkNickname(@RequestParam("nickname") String nickname, Principal principal) {
        Member member = memberRepository.findByNickName(nickname);

        if(member == null)
            return 0;
        else{
            if(principal != null && member.getUserId().equals(principal.getName()))
                return 0;

            return 1;
        }

    }

    @PostMapping("/login")
    public ResponseEntity<Long> loginProc(@RequestBody Member member
                                        , HttpServletRequest request
                                        , HttpServletResponse response) {

        return new ResponseEntity<>(memberService.memberLogin(member, request, response), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public int logout(HttpServletRequest request
                    , HttpServletResponse response
                    , Principal principal){

        log.info("logout controller");

        return memberService.logout(request, response, principal);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Long> modifyProfile(@RequestParam("nickname") String nickname
                                            , @RequestParam(value = "profileThumbnail", required = false) MultipartFile profileThumbnail
                                            , @RequestParam(value = "deleteProfile", required = false) String deleteProfile
                                            , Principal principal) {

        ProfileDTO profileDTO = ProfileDTO.builder()
                                    .nickname(nickname)
                                    .profileThumbnail(profileThumbnail)
                                    .deleteProfile(deleteProfile)
                                    .build();

        return new ResponseEntity<>(memberService.modifyProfile(profileDTO, principal), HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> getProfile(Principal principal) {

        return new ResponseEntity<>(memberService.getProfile(principal), HttpStatus.OK);
    }
}
