package com.example.petstable.domain.pet.entity;

import com.example.petstable.domain.member.entity.BaseTimeEntity;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.pet.dto.request.PetRegisterNewPetRequest;
import com.example.petstable.domain.pet.dto.request.PetRegisterRequest;
import com.example.petstable.domain.pet.dto.request.PetUpdateRequest;
import com.example.petstable.domain.pet.dto.response.PetInfoResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static jakarta.persistence.FetchType.*;

@Entity
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "pet")
public class PetEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Long id;

    private String name; // 이름
    private int age; // 나이
    private LocalDate birth; // 생년월일
    private String ageApproximation; // 이건 생년월일 정확하지 않을 때 대체
    private String size; // 크기 ( 소형, 중형, 대형 )
    private String kind; // 품종 ( 푸들, 말티즈, 닥스훈트, ... )
    private String gender; // 성별
    private String walk; // 산책량
    private String image_url; // 프로필 사진

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    // 연관관계 메서드
    public void setMember(MemberEntity member) {
        this.member = member;
        member.getPets().add(this);
    }

    // 생성 메서드 - 로그인 이후 최초 반려동물 등록
    public static PetEntity createPet(PetRegisterRequest petRegisterRequest) {
        return PetEntity.builder()
                .name(petRegisterRequest.getName())
                .age(petRegisterRequest.getAge())
                .size(petRegisterRequest.getSize())
                .build();
    }

    // 생년월일 혹은 대체 생년월일 설정 및 나이 계산
    private void setAgeAndBirth(String birth, String ageApproximation) {

        if (birth != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birthDate = LocalDate.parse(birth, formatter);
            this.birth = birthDate;
            this.age = getAge(birthDate, LocalDate.now());

        } else if (ageApproximation != null) {
            this.ageApproximation = ageApproximation;
            this.age = getAgeApprox(ageApproximation);
        }
    }

    // 새로운 반려동물 등록
    public static PetEntity createNewPet(PetRegisterNewPetRequest petRegisterNewPetRequest) {

        PetEntity pet = PetEntity.builder()
                .name(petRegisterNewPetRequest.getName())
                .age(petRegisterNewPetRequest.getAge())
                .size(petRegisterNewPetRequest.getSize())
                .kind(petRegisterNewPetRequest.getKind())
                .gender(petRegisterNewPetRequest.getGender())
                .walk(petRegisterNewPetRequest.getWalk())
                .build();

        pet.setAgeAndBirth(petRegisterNewPetRequest.getBirth(), petRegisterNewPetRequest.getAgeApproximation());

        return pet;
    }

    // 기존 반려동물 정보 업데이트
    public void updatePet(PetUpdateRequest request) {

        this.setAgeAndBirth(request.getBirth(), request.getAgeApproximation());
        this.kind = request.getKind();
        this.gender = request.getGender();
        this.walk = request.getWalk();
        this.image_url = request.getImage_url();
    }

    // "yyyy-mm-dd" 포맷의 나이 계산
    private static int getAge(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
    }

    // "N년 M개월" 포맷의 나이 계산
    private static int getAgeApprox(String ageApproximation) {

        if (ageApproximation != null && ageApproximation.contains("년")) {
            String[] parts = ageApproximation.split("년");
            try {
                return Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    // 생일이 지나면 1살 추가
    public int increaseAge() {
        return this.age += 1;
    }

    // DTO 변환
    public static PetInfoResponse toPetInfoResponse(PetEntity petEntity) {
        return PetInfoResponse.builder()
                .id(petEntity.getId())
                .name(petEntity.getName())
                .age(petEntity.getAge())
                .size(petEntity.getSize())
                .kind(petEntity.getKind())
                .gender(petEntity.getGender())
                .walk(petEntity.getWalk())
                .ownerNickname(petEntity.getMember().getNickName())
                .build();
    }

    public void updateImage(String imageUrl) {
        this.image_url = imageUrl;
    }
}
