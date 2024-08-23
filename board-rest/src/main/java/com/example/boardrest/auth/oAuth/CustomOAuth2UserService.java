package com.example.boardrest.auth.oAuth;

import com.example.boardrest.auth.response.*;
import com.example.boardrest.auth.oAuth.domain.OAuth2DTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.OAuthProvider;
import com.example.boardrest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = createProviderObject(registrationId, oAuth2User);
        String userId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        Member existsData = memberRepository.findByUserId(userId);

        if(existsData == null) {
            Member member = OAuth2ResponseEntityConverter.toEntity(oAuth2Response, userId);
            member.addAuth();
            member.setUsername(oAuth2Response.getName());
            existsData = member;
        }else{
            existsData.setEmail(oAuth2Response.getEmail());
            existsData.setUsername(oAuth2Response.getName());
        }

        memberRepository.save(existsData);
        OAuth2DTO oAuth2DTO = new OAuth2DTO(existsData);

        return new CustomOAuth2User(oAuth2DTO);
    }

    private OAuth2Response createProviderObject(String registrationId, OAuth2User oAuth2User){
        if(registrationId.equals(OAuthProvider.NAVER.getKey()))
            return new NaverResponse(oAuth2User.getAttributes());
        else if(registrationId.equals(OAuthProvider.GOOGLE.getKey()))
            return new GoogleResponse(oAuth2User.getAttributes());
        else if(registrationId.equals(OAuthProvider.KAKAO.getKey()))
            return new KakaoResponse(oAuth2User.getAttributes());
        else
            throw new BadCredentialsException("OAuth2 BadCredentials");
    }
}
