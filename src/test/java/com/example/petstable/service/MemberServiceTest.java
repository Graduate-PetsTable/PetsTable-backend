package com.example.petstable.service;

import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.global.exception.PetsTableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("유저 로그인 후 정보를 입력받아 회원가입")
    void signUpByOAuthMember() {
        String email = "ssg@naver.com";
        String socialId = "123456789";

        MemberEntity member = MemberEntity.builder()
                .email(email)
                .socialType(SocialType.APPLE)
                .socialId(socialId)
                .build();

        MemberEntity savedMember = memberRepository.save(member);

        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest("Seung", SocialType.APPLE.getValue(), socialId);

        memberService.signUpByOAuthMember(request);

        MemberEntity actual = memberRepository.findByEmail(savedMember.getEmail()).orElseThrow();
        assertThat(actual.getNickName()).isEqualTo("Seung");
    }

    @Test
    @DisplayName("로그인 후 회원 가입 시 socialType, socialId 정보로 회원이 존재하지 않으면 예외를 반환")
    void signUpByOAuthMemberWhenInvalidPlatformInfo() {
        String email = "ssg@naver.com";
        String socialId = "123456789";

        MemberEntity savedMember = MemberEntity.builder()
                .email(email)
                .socialType(SocialType.APPLE)
                .socialId(socialId)
                .build();

        memberRepository.save(savedMember);

        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest("Seung", SocialType.APPLE.getValue(), "invalid");

        assertThatThrownBy(() -> memberService.signUpByOAuthMember(request))
                .isInstanceOf(PetsTableException.class);
    }
}
