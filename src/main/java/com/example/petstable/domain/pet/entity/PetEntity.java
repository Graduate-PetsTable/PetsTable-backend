package com.example.petstable.domain.pet.entity;

import com.example.petstable.domain.member.entity.BaseTimeEntity;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.pet.dto.request.PetInfoRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    private String type; // 종 ( 강아지, 고양이 )
    private String name; // 이름
    private int age; // 나이
    private String size; // 크기 ( 소형, 중형, 대형 )
    private String kind; // 품종 ( 푸들, 말티즈, 닥스훈트, ... )
    private String gender; // 성별
    private String walk; // 산책량

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    // 연관관계 메서드
    public void setMember(MemberEntity member) {
        this.member = member;
        member.getPets().add(this);
    }

    // 생성 메서드
    public static PetEntity createPet(PetInfoRequest petInfoRequest) {

        return PetEntity.builder()
                .type(petInfoRequest.getType())
                .name(petInfoRequest.getName())
                .age(petInfoRequest.getAge())
                .size(petInfoRequest.getSize())
                .kind(petInfoRequest.getKind())
                .gender(petInfoRequest.getGender())
                .walk(petInfoRequest.getWalk())
                .build();
    }
}
