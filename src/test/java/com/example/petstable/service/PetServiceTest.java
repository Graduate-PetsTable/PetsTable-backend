package com.example.petstable.service;

import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.domain.pet.dto.request.PetInfoRequest;
import com.example.petstable.domain.pet.dto.response.PetInfoResponse;
import com.example.petstable.domain.pet.entity.PetEntity;
import com.example.petstable.domain.pet.repository.PetRepository;
import com.example.petstable.domain.pet.service.PetService;
import com.example.petstable.global.exception.PetsTableException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PetServiceTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PetService petService;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("반려동물 정보를 입력받아서 등록하기")
    void registerPet() {
        String email = "ssg@naver.com";
        String socialId = "123456789";

        MemberEntity member = MemberEntity.builder()
                .email(email)
                .socialType(SocialType.APPLE)
                .socialId(socialId)
                .build();

        memberRepository.save(member);
        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest(null, "Seung", SocialType.APPLE.getValue(), socialId);
        memberService.signUpByOAuthMember(request);

        PetInfoRequest petRequest =PetInfoRequest.builder()
                .name("파랑이")
                .kind("말티즈")
                .type("강아지")
                .build();

        PetInfoResponse petInfoResponse = petService.registerPet(member.getId(), petRequest);

        PetEntity actual = petRepository.findByName("파랑이").orElseThrow();
        Assertions.assertThat(actual.getKind()).isEqualTo("말티즈");
    }

    @Test
    @DisplayName("추가로 입력한 반려동물 정보가 이미 회원의 반려동물 정보에 있을 때 ( 중복일 때 )")
    void validDuplicatePet() {
        String email = "ssg@naver.com";
        String socialId = "123456789";

        MemberEntity member = MemberEntity.builder()
                .email(email)
                .socialType(SocialType.APPLE)
                .socialId(socialId)
                .build();

        memberRepository.save(member);
        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest(null, "Seung", SocialType.APPLE.getValue(), socialId);
        memberService.signUpByOAuthMember(request);

        PetInfoRequest petRequest =PetInfoRequest.builder()
                .name("파랑이")
                .kind("말티즈")
                .type("강아지")
                .build();

        PetEntity petEntity = PetEntity.builder()
                .name("파랑이")
                .kind("말티즈")
                .type("강아지")
                .build();

        petRepository.save(petEntity);
        Assertions.assertThatThrownBy(() -> petService.registerPet(member.getId(), petRequest))
                .isInstanceOf(PetsTableException.class);
    }

}
