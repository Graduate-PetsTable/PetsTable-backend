package com.example.petstable.service;

import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.member.service.AuthService;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.domain.pet.dto.request.PetRegisterRequest;
import com.example.petstable.domain.pet.dto.response.PetInfoResponse;
import com.example.petstable.domain.pet.dto.response.PetRegisterResponse;
import com.example.petstable.domain.pet.entity.PetEntity;
import com.example.petstable.domain.pet.repository.PetRepository;
import com.example.petstable.domain.pet.service.PetService;
import com.example.petstable.global.exception.PetsTableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PetServiceTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthService authService;

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
        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest("Seung", SocialType.APPLE.getValue(), socialId);
        memberService.signUpByOAuthMember(request);

        PetRegisterRequest petRequest = PetRegisterRequest.builder()
                .name("파랑이")
                .kind("말티즈")
                .type("강아지")
                .build();

        PetRegisterResponse petRegisterResponse = petService.registerPet(member.getId(), petRequest);

        PetEntity actual = petRepository.findByName("파랑이").orElseThrow();
        assertThat(actual.getKind()).isEqualTo("말티즈");
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
        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest("Seung", SocialType.APPLE.getValue(), socialId);
        memberService.signUpByOAuthMember(request);

        PetRegisterRequest petRequest = PetRegisterRequest.builder()
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
        assertThatThrownBy(() -> petService.registerPet(member.getId(), petRequest))
                .isInstanceOf(PetsTableException.class);
    }

    @Test
    @DisplayName("반려동물 등록 후 전체조회 시 정상적으로 출력 되어야함")
    void getAllPets() {

        // given
        String email = "ssg@naver.com";
        String socialId = "123456789";

        MemberEntity member = MemberEntity.builder()
                .email(email)
                .socialType(SocialType.APPLE)
                .socialId(socialId)
                .build();

        MemberEntity saveMember = memberRepository.save(member);

        PetRegisterRequest petRegisterRequest1 = PetRegisterRequest.builder()
                .name("파랑이")
                .age(6)
                .kind("말티즈")
                .type("강아지")
                .size("소형")
                .build();

        String expected1 = "테스트";
        PetRegisterRequest petRegisterRequest2 = PetRegisterRequest.builder()
                .name(expected1)
                .age(6)
                .kind("말티즈")
                .type("강아지")
                .size("소형")
                .build();

        petService.registerPet(member.getId(), petRegisterRequest1);
        petService.registerPet(member.getId(), petRegisterRequest2);

        // when
        List<PetInfoResponse> actual2 = petService.getAllMyPets(saveMember.getId());
        int expected2 = 2;

        PetEntity actual1 = petRepository.findByName(expected1).orElseThrow();

        String expected3 = saveMember.getNickName();

        // then
        assertThat(actual1.getName()).isEqualTo(expected1);
        assertThat(actual2.size()).isEqualTo(expected2);
        assertThat(actual2.get(0).getOwnerNickname()).isEqualTo(expected3);
    }


    @Test
    @DisplayName("반려동물 전체 조회 했을 때 해당 회원이 존재하지 않을 경우 예외 반환")
    void validMemberByPets() {

        // given
        String email = "ssg@naver.com";
        String socialId = "123456789";

        MemberEntity member = MemberEntity.builder()
                .email(email)
                .socialType(SocialType.APPLE)
                .socialId(socialId)
                .build();

        MemberEntity saveMember = memberRepository.save(member);

        PetRegisterRequest petRegisterRequest = PetRegisterRequest.builder()
                .name("파랑이")
                .age(6)
                .kind("말티즈")
                .type("강아지")
                .size("소형")
                .build();


        // when
        petService.registerPet(saveMember.getId(), petRegisterRequest);

        // then
        assertThatThrownBy(() -> petService.getAllMyPets(3L)).isInstanceOf(PetsTableException.class);

    }

    @Test
    @DisplayName("회원 id 와 반려동물 id 로 해당 반려동물의 상세 정보 조회")
    void getInfoByMemberPet() {

        // given
        String email = "ssg@naver.com";
        String socialId = "123456789";

        MemberEntity member = MemberEntity.builder()
                .email(email)
                .socialType(SocialType.APPLE)
                .socialId(socialId)
                .build();

        MemberEntity saveMember = memberRepository.save(member);

        PetRegisterRequest registerPet = PetRegisterRequest.builder()
                .name("파랑이")
                .age(6)
                .kind("말티즈")
                .type("강아지")
                .size("소형")
                .build();

        PetRegisterResponse expected = petService.registerPet(member.getId(), registerPet);

        // when
        PetInfoResponse actual = petService.getMyPetInfo(saveMember.getId(), expected.getId());


        // then
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getKind()).isEqualTo(expected.getKind());
    }

}
