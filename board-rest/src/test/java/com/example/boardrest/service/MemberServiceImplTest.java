package com.example.boardrest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.domain.dto.MemberDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.Cookie;
import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceImplTest {

    @Value("#{jwt['token.prefix']}")
    private String tokenPrefix;

    @Value("#{jwt['token.access.secret']}")
    private String accessSecret;

    /**
     * 비밀번호가 10자리가 넘어가니 제대로 검증하지 못하는 오류가 발생.
     * 처음에는 특수문자 문제인가 싶었지만 몇가지 테스트를 거쳐보니 그게 아니라는것을 알았음.
     *
     * BCryptPasswordEncoder를 config에 빈으로 등록해놓고 사용중인데
     * 10자리가 넘어가면서부터 제대로 검증하지를 못한다.
     * 여기저기 찾아봤지만 해결책은 일단 안보이고
     * 다른 방법으로는 BCrypt.hasPw(password, BCrypt.getsalt());
     * 이렇게 처리하게 되면 일단 테스트에서는 true로 출력은 할 수 있다.
     *
     * 하지만 문제점.
     * customUser에서 처리하는 과정 중 PasswordEncoder를 통해 처리하는 것을 디버그로 확인할 수 있었는데
     * 그럼 여기에 BCrypt로 처리하도록 변경할 수 있는 방법이 있는가.
     * 아니면 직접 코드를 다 구현해야 하는것인가
     * 그것도 아니라면 passwordEncoder에서 salt 옵션을 설정할 수 있는 방법이 존재하는가.
     *
     * 이걸 해결해야 문제가 풀릴듯.
     *
     * 동일한 10자리 비밀번호이더라도 특수문자가 없으면 문제가 없으나
     * 특수문자가 들어가면 오류 발생.
     *
     * 문제 해결.
     * 클라이언트 서버에서 비밀번호가 넘어올때
     * password, password 형태로 두번 중복으로 넘어왔기 때문.
     * 두번이 넘어온 이유는 비밀번호 확인 input name을 동일하게 설정했기 때문이다.
     *
     * 일단 현재 문제는 해결이 되었고
     * 추가적으로 테스트가 좀 필요하긴 함.
     * 테스트 메소드에서도 오류가 발생한 경우가 있었기 때문에 좀 더 테스트 해볼것.
     */
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @DisplayName("토큰 생성 후 복호화 테스트")
    @Test
    void tokenTest () {
        String accessToken = JWT.create()
                .withSubject("cocoToken")
                .withExpiresAt(new Date(System.currentTimeMillis()))
                .withClaim("userId", "coco")
                .sign(Algorithm.HMAC512(accessSecret));

        System.out.println("token : " + accessToken);

        try{
            System.out.println("sleep");
            Thread.sleep(2000);
            System.out.println("sleep end");
        }catch (Exception e) {
            e.printStackTrace();
        }

        String verifyUid;

        try {
            verifyUid = JWT.require(Algorithm.HMAC512(accessSecret))
                    .build()
                    .verify(accessToken)
                    .getClaim("userId")
                    .asString();
        }catch (TokenExpiredException e) {
            System.out.println("TokenExpiredException!!");
            verifyUid = "exception!!";
        }

        String decodeUid = JWT.decode(accessToken).getClaim("userId").asString();

        assertEquals("coco", decodeUid);
        assertEquals("exception!!", verifyUid);

//        System.out.println("decodeUid : " + decodeUid);
//        System.out.println("verifyUid : " + verifyUid);
    }

    @Test
    void loginTest(){
        Member member = Member.builder()
                .userId("testuser5")
                .userPw("zhzhahWl!2")
                .build();

//        JwtDTO dto = memberService.memberLogin(member);


//        System.out.println(dto);
    }


    @Test
    void joinMember(){
        /*MemberDTO member = MemberDTO.builder()
                .userId("testuser4")
                .userPw("zhzhahWl!2")
                .userName("testuser4")
                .build();

        memberService.memberJoinProc(member);*/

        /*Member member = Member.builder()
                .userId("testuser9")
                .userPw(BCrypt.hashpw("zhzhahWl!2", BCrypt.gensalt()))
                .userName("testuser9")
                .build();

        memberRepository.save(member);

        Member memberData = memberRepository.findByUserId("testuser9");

        System.out.println(BCrypt.checkpw("zhzhahWl!2", memberData.getUserPw()));*/

        /*MemberDTO member = MemberDTO.builder()
                .userId("testuser9")
                .userPw("zhzhahWl!2")
                .userName("testuser9")
                .build();

        memberService.memberJoinProc(member);

        Member memberData = memberRepository.findByUserId("testuser9");

        System.out.println(passwordEncoder.matches("zhzhahWl!2", memberData.getUserPw()));*/

    }





}