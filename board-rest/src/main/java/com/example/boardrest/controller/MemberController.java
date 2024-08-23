package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.member.out.LoginStateDTO;
import com.example.boardrest.domain.dto.member.in.JoinDTO;
import com.example.boardrest.domain.dto.member.in.ProfileDTO;
import com.example.boardrest.domain.dto.member.out.ProfileResponseDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    @GetMapping("/check-login")
    public ResponseEntity<LoginStateDTO> checkLogin(Principal principal) {

        LoginStateDTO responseDTO = new LoginStateDTO(principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PostMapping("/join")
    public String joinProc(@RequestPart JoinDTO joinDTO
                        , @RequestParam(value = "profileThumbnail", required = false) MultipartFile profileThumbnail){

        return memberService.memberJoinProc(joinDTO, profileThumbnail);
    }

    @GetMapping("/check-id")
    public String checkUserId(@RequestParam("userId") String userId){

        return memberService.checkId(userId);
    }

    @GetMapping("/check-nickname")
    public String checkNickname(@RequestParam("nickname") String nickname, Principal principal) {

        return memberService.checkNickname(nickname, principal);
    }

    //member entity로 받는게 아니라 DTO로 받도록 수정.
    @PostMapping("/login")
    public ResponseEntity<String> loginProc(@RequestBody Member member
                                        , HttpServletRequest request
                                        , HttpServletResponse response) {

        return new ResponseEntity<>(memberService.memberLogin(member, request, response), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public String logout(HttpServletRequest request
                    , HttpServletResponse response
                    , Principal principal){

        return memberService.logout(request, response, principal);
    }

    @PatchMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<String> modifyProfile(@RequestParam("nickname") String nickname
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
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ProfileResponseDTO> getProfile(Principal principal) {

        return new ResponseEntity<>(memberService.getProfile(principal), HttpStatus.OK);
    }
}
