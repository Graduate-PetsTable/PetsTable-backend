package com.example.petstable.service;

import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.member.service.AuthService;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.domain.pet.dto.request.PetRegisterNewPetRequest;
import com.example.petstable.domain.pet.dto.request.PetRegisterRequest;
import com.example.petstable.domain.pet.dto.request.PetUpdateRequest;
import com.example.petstable.domain.pet.dto.response.PetInfoResponse;
import com.example.petstable.domain.pet.dto.response.PetRegisterNewPetResponse;
import com.example.petstable.domain.pet.dto.response.PetRegisterResponse;
import com.example.petstable.domain.pet.entity.PetEntity;
import com.example.petstable.domain.pet.repository.PetRepository;
import com.example.petstable.domain.pet.service.PetService;
import com.example.petstable.global.exception.PetsTableException;
import com.example.petstable.global.support.AwsS3Uploader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PetServiceTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private AwsS3Uploader awsS3Uploader;

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
                .size("소형")
                .build();

        petService.registerPet(member.getId(), petRequest);

        PetEntity actual = petRepository.findByName("파랑이").orElseThrow();
        assertThat(actual.getSize()).isEqualTo("소형");
    }

    @Test
    @DisplayName("추가로 입력한 반려동물 정보가 이미 회원의 반려동물 정보에 있을 때 ( 중복일 때 )")
    void validDuplicatePet() {
        MemberEntity member = MemberEntity.builder()
                .nickName("테스트")
                .build();
        memberRepository.save(member);

        PetRegisterNewPetRequest newPetRequest = PetRegisterNewPetRequest.builder()
                .name("파랑이")
                .kind("말티즈")
                .build();

        PetEntity petEntity = PetEntity.builder()
                .name("파랑이")
                .kind("말티즈")
                .member(member)
                .build();

        petRepository.save(petEntity);
        assertThatThrownBy(() -> petService.registerNewPet(member.getId(), newPetRequest))
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
                .size("소형")
                .build();

        String expected1 = "테스트";
        PetRegisterRequest petRegisterRequest2 = PetRegisterRequest.builder()
                .name(expected1)
                .age(6)
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
                .size("소형")
                .build();

        PetRegisterResponse expected = petService.registerPet(member.getId(), registerPet);

        // when
        PetInfoResponse actual = petService.getMyPetInfo(saveMember.getId(), expected.getId());


        // then
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getKind()).isEqualTo(expected.getKind());
    }


    @DisplayName("반려동물 사진 등록에 성공한다.")
    @Test
    void addPetImage() throws IOException {

        // given
        MemberEntity member = MemberEntity.builder()
                .nickName("sg")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        PetEntity pet = PetEntity.builder()
                .name("파랑이")
                .member(member)
                .build();

        petRepository.save(pet);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("test_img", "test_img.jpg", "jpg", new FileInputStream("src/test/resources/images/test_img.jpg"));
        when(awsS3Uploader.uploadImage(mockMultipartFile)).thenReturn("test_img.jpg");

        // when
        petService.registerPetImage(member.getId(), pet.getId(), mockMultipartFile);
        PetEntity actual = petRepository.findById(pet.getId()).orElseThrow();

        // then
        assertThat(actual.getImage_url()).isEqualTo("test_img.jpg");
    }

    @DisplayName("반려동물 사진 삭제에 성공한다.")
    @Test
    void deletePetImage() {

        // given
        MemberEntity member = MemberEntity.builder()
                .nickName("sg")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        PetEntity pet = PetEntity.builder()
                .name("파랑이")
                .image_url("test_img.jpg")
                .member(member)
                .build();

        petRepository.save(pet);

        // when
        petService.deletePetImage(member.getId(), pet.getId());
        PetEntity actual = petRepository.findById(pet.getId()).orElseThrow();

        // then
        assertThat(actual.getImage_url()).isNull();
    }

    @DisplayName("새로운 반려동물 등록에 성공한다. 단, 생년월일을 기입하며 나이도 자동계산 된다.")
    @Test
    void registerNewPetWithBirth() {

        // given
        MemberEntity member = MemberEntity.builder()
                .nickName("회원1")
                .build();

        memberRepository.save(member);

        String birth = "2020-08-10";
        LocalDate current = LocalDate.now();

        PetRegisterNewPetRequest request = PetRegisterNewPetRequest.builder()
                .name("파랑이")
                .kind("말티즈")
                .birth(birth)
                .build();

        petService.registerNewPet(member.getId(), request);

        // when
        PetEntity actual = petRepository.findByName("파랑이").orElseThrow();

        // then
        System.out.println(birth);
        System.out.println(current);

        assertThat(actual.getBirth()).isEqualTo(birth);
        assertThat(actual.getAge()).isEqualTo(4);
    }

    @DisplayName("새로운 반려동물 등록에 성공한다. 단, 생년월일이 기억나지 않아 대체나이를 기입한다.")
    @Test
    void registerNewPetWithAgeApproximation() {

        // given
        MemberEntity member = MemberEntity.builder()
                .nickName("하하")
                .build();
        memberRepository.save(member);

        PetRegisterNewPetRequest request = PetRegisterNewPetRequest.builder()
                .name("파랑이")
                .kind("말티즈")
                .ageApproximation("2년 6개월")
                .build();

        petService.registerNewPet(member.getId(), request);

        // when
        PetEntity actual = petRepository.findByName("파랑이").orElseThrow();

        // then
        assertThat(actual.getAgeApproximation()).isEqualTo("2년 6개월");
        assertThat(actual.getAge()).isEqualTo(2);
    }
}
