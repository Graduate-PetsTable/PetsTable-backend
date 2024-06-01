package com.example.petstable.domain.member.entity;

import com.example.petstable.domain.pet.entity.PetEntity;
import com.example.petstable.global.exception.PetsTableException;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.regex.Pattern;

import static com.example.petstable.global.exception.message.MemberMessage.INVALID_NICKNAME;

@Entity
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "member")
public class MemberEntity extends BaseTimeEntity {

    // 알파벳(대소문자), 한글 자음, 한글 모음으로 이루어져 있으며 2자에서 6자 사이
    private static final Pattern NICKNAME_REGEX = Pattern.compile("^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]{2,6}$");

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email; // 이메일
    private String nickName; // 닉네임

    private String image_url;

    @Enumerated(value = EnumType.STRING)
    private SocialType socialType; // APPLE, GOOGLE
    private String socialId; // Claims 에 담긴 subject

    @Enumerated(value = EnumType.STRING)
    private RoleType role; // ADMIN, MEMBER

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "member")
    private List<PetEntity> pets;

    // 연관 관계 메서드
    public void addPets(PetEntity pet) {
        pets.add(pet);
        pet.setMember(this);
    }

    // 닉네임 검증
    private void validateNickname(String nickname) {
        if (!NICKNAME_REGEX.matcher(nickname).matches()) {
            throw new PetsTableException(INVALID_NICKNAME.getStatus(), INVALID_NICKNAME.getMessage(), 400);
        }
    }

    // 회원가입 - 이메일, 닉네임으로
    public void registerOAuthMember(String email, String nickname) {
        validateNickname(nickname);
        this.nickName = nickname;
        if (email != null) {
            this.email = email;
        }
    }

    public boolean isRegisteredOAuthMember() {
        return nickName != null;
    }
}
