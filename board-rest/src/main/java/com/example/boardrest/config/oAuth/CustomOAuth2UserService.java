package com.example.boardrest.config.oAuth;

import com.example.boardrest.config.oAuth.response.GoogleResponse;
import com.example.boardrest.config.oAuth.response.KakaoResponse;
import com.example.boardrest.config.oAuth.response.NaverResponse;
import com.example.boardrest.config.oAuth.response.OAuth2Response;
import com.example.boardrest.domain.dto.OAuth2DTO;
import com.example.boardrest.domain.entity.Auth;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.Role;
import com.example.boardrest.repository.AuthRepository;
import com.example.boardrest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    private final AuthRepository authRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if(registrationId.equals("naver"))
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        else if(registrationId.equals("google"))
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        else if(registrationId.equals("kakao"))
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

        String userId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        Member existsData = memberRepository.findByUserId(userId);
        OAuth2DTO oAuth2DTO;

        if(existsData == null) {
            Member member = Member.builder()
                                .userId(userId)
                                .email(oAuth2Response.getEmail())
                                .username(oAuth2Response.getName())
                                .provider(oAuth2Response.getProvider())
                                .build();

            member.addAuth(
                    Auth.builder()
                            .auth(Role.MEMBER.getKey())
                            .build()
            );

            memberRepository.save(member);

            /*authRepository.save(Auth.builder()
                    .auth(Role.MEMBER.toString())
                    .member(Member.builder().id(uid).build())
                    .build()
            );*/

            List<Auth> authList = new ArrayList<>();
            authList.add(Auth.builder().auth(Role.MEMBER.getKey()).build());

            oAuth2DTO = OAuth2DTO.builder()
                    .userId(userId)
                    .username(oAuth2Response.getName())
                    .authList(authList)
                    .nickname(null)
                    .build();
        }else{
            existsData.setEmail(oAuth2Response.getEmail());
            existsData.setUsername(oAuth2Response.getName());

            memberRepository.save(existsData);

            List<Auth> authList = existsData.getAuths();

            oAuth2DTO = OAuth2DTO.builder()
                    .userId(existsData.getUserId())
                    .username(existsData.getUsername())
                    .authList(authList)
                    .nickname(existsData.getNickName())
                    .build();
        }

        return new CustomOAuth2User(oAuth2DTO);
    }
}
