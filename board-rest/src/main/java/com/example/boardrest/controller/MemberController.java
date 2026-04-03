package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.member.in.JoinRequest;
import com.example.boardrest.domain.dto.member.in.OAuthJoinRequest;
import com.example.boardrest.domain.dto.member.in.UpdateProfileRequest;
import com.example.boardrest.domain.dto.member.out.MemberStatusResponse;
import com.example.boardrest.domain.dto.member.out.ProfileResponse;
import com.example.boardrest.domain.dto.response.ApiResponse;
import com.example.boardrest.domain.enumuration.MemberCheckResult;
import com.example.boardrest.domain.enumuration.ResponseStatus;
import com.example.boardrest.properties.CookieProperties;
import com.example.boardrest.service.AuthContextService;
import com.example.boardrest.service.ImageFileService;
import com.example.boardrest.service.JwtTokenProvider;
import com.example.boardrest.service.MemberService;
import com.example.boardrest.validator.MemberRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    private final AuthContextService authContextService;

    private final JwtTokenProvider tokenProvider;

    private final CookieProperties cookieProperties;

    private final ImageFileService imageFileService;

    private final MemberRequestValidator memberRequestValidator;

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MemberStatusResponse>> checkLogin(Authentication authentication) {
        MemberStatusResponse content = authContextService.getMemberStatus(authentication);

        return ResponseEntity
                    .ok(
                        ApiResponse.success(
                                content,
                                ResponseStatus.SUCCESS.getMessage()
                        )
                    );
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @AuthenticationPrincipal String userId
    ){
        // AuthorizationFilter 검증 이후 PreAuthorize isAuthenticated로 인해 ino cookie는 null일 수 없음이 검증되므로 NPE 불가라고 판단.
        String inoValue = WebUtils.getCookie(request, cookieProperties.getIno().getHeader()).getValue();

        try {
            tokenProvider.deleteTokenData(userId, inoValue);
        }catch (Exception e) {
            log.warn("Failed to delete Redis token for user: {}, ino: {}. Error: ", userId, inoValue, e);
        }finally {
            tokenProvider.deleteTokenCookie(response);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     *
     * @param joinRequest
     * @return
     *
     * valid 대신 별도의 validator 사용.
     * 프론트 검증 이후 넘어오기 때문에 정상적인 요청이라면 절대 오류가 발생할 수 없음.
     *
     * 요구사항에 따른 변경이 발생할 수 있는 사항으로는
     * 1. swagger에 노출 시 어떤 필드가 잘못되었는지 알 수 있어야 한다.
     */
    @PostMapping(value = "/join", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> joinProc(@ModelAttribute JoinRequest joinRequest){
        memberRequestValidator.validateJoinRequest(joinRequest);
        memberService.register(joinRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *
     * @param request
     * @param principal
     * @return
     *
     * join과 마찬가지로 validator를 통해 검증
     */
    @PostMapping(value = "/oauth/join/profile", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> postOAuthProfile(@ModelAttribute OAuthJoinRequest request,
                                                 Principal principal) {

        memberRequestValidator.validateOAuthRequest(request);
        memberService.oAuthJoin(request, principal.getName());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-id/{userId}")
    public ResponseEntity<ApiResponse<Void>> checkUserId(@PathVariable(name = "userId") String userId){
        MemberCheckResult result = memberService.checkId(userId);

        return ResponseEntity.ok(ApiResponse.success(result.getMessage()));
    }

    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<ApiResponse<Void>> checkNickname(@PathVariable(name = "nickname") String nickname, Principal principal) {

        MemberCheckResult result = memberService.checkNickname(nickname, principal);

        if(result == MemberCheckResult.VALID)
            return ResponseEntity.ok(ApiResponse.success(result.getMessage()));
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.success(result.getMessage()));
    }

    @PatchMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateProfile(@ModelAttribute UpdateProfileRequest profileRequest,
                                            Principal principal) {
        memberRequestValidator.validateUpdateProfile(profileRequest);
        memberService.updateProfile(profileRequest, principal);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(Principal principal) {

        ProfileResponse result = memberService.getProfile(principal);

        return ResponseEntity.ok(ApiResponse.success(result, ResponseStatus.SUCCESS.getMessage()));
    }

    @GetMapping("/display/{imageName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String imageName){

        return imageFileService.getProfileImageDisplay(imageName);
    }
}
