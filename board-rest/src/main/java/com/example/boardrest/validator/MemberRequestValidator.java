package com.example.boardrest.validator;

import com.example.boardrest.customException.CustomInvalidJoinPolicyException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.member.in.JoinRequest;
import com.example.boardrest.domain.dto.member.in.OAuthJoinRequest;
import com.example.boardrest.domain.dto.member.in.UpdateProfileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class MemberRequestValidator {

    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{5,15}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=-])(?=.*[0-9]).{8,16}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]{2,15}$");

    private void validateUserId(String userId) {
        if(userId == null || !USER_ID_PATTERN.matcher(userId).matches())
            throw new CustomInvalidJoinPolicyException(ErrorCode.BAD_REQUEST, "userId Invalid");
    }

    private void validatePassword(String password) {
        if(password == null || !PASSWORD_PATTERN.matcher(password).matches())
            throw new CustomInvalidJoinPolicyException(ErrorCode.BAD_REQUEST, "password Invalid");
    }

    private void validateEmail(String email) {
        if(email == null || !EMAIL_PATTERN.matcher(email).matches())
            throw new CustomInvalidJoinPolicyException(ErrorCode.BAD_REQUEST, "email Invalid");
    }

    private void validateUserName(String userName) {
        if(userName == null || userName.length() < 2)
            throw new CustomInvalidJoinPolicyException(ErrorCode.BAD_REQUEST, "userName Invalid");
    }

    private void validateNickname(String nickname) {
        if(nickname != null && !NICKNAME_PATTERN.matcher(nickname).matches())
            throw new CustomInvalidJoinPolicyException(ErrorCode.BAD_REQUEST, "nickname Invalid");
    }

    public void validateJoinRequest(JoinRequest request) {
        log.info("MemberRequestValidator.validateJoinRequest");
        validateUserId(request.getUserId());
        validatePassword(request.getPassword());
        validateUserName(request.getUserName());
        validateNickname(request.getNickname());
        validateEmail(request.getEmail());
    }

    public void validateOAuthRequest(OAuthJoinRequest request) {
        log.info("MemberRequestValidator.validateOAuthRequest");
        validateNickname(request.getNickname());
    }

    public void validateUpdateProfile(UpdateProfileRequest request) {
        log.info("MemberRequestValidator.validateUpdateProfile");
        validateNickname(request.getNickname());
        validateNickname(request.getEmail());
    }
}
