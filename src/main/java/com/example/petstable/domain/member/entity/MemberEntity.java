package com.example.petstable.domain.member.entity;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.bookmark.entity.BookmarkEntity;
import com.example.petstable.domain.pet.entity.PetEntity;
import com.example.petstable.domain.report.entity.ReportEntity;
import com.example.petstable.global.exception.PetsTableException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Pattern;

import static com.example.petstable.domain.member.message.MemberMessage.INVALID_NICKNAME;

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

    private String image_url; // 프로필 이미지
    private int report_count; // 신고 횟수

    @Enumerated(value = EnumType.STRING)
    private SocialType socialType; // APPLE, GOOGLE
    private String socialId; // Claims 에 담긴 subject

    @Enumerated(value = EnumType.STRING)
    private RoleType role; // ADMIN, MEMBER

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "member")
    private List<PetEntity> pets;

    @OneToMany(mappedBy = "member")
    private List<BoardEntity> posts;

    @OneToMany(mappedBy = "member")
    private List<BookmarkEntity> bookmarks;

    @OneToMany(mappedBy = "reporter")
    private List<ReportEntity> report;

    // 연관 관계 메서드
    public void addPets(PetEntity pet) {
        pets.add(pet);
        pet.setMember(this);
    }

    public void addPost(BoardEntity post) {
        posts.add(post);
        post.setMember(this);
    }

    public void addBookmark(BookmarkEntity bookmark) {
        bookmarks.add(bookmark);
        bookmark.setMember(this);
    }

    // 닉네임 검증
    private void validateNickname(String nickname) {
        if (!NICKNAME_REGEX.matcher(nickname).matches()) {
            throw new PetsTableException(INVALID_NICKNAME.getStatus(), INVALID_NICKNAME.getMessage(), 400);
        }
    }

    // 회원가입 - 이메일, 닉네임으로
    public void updateNickname(String nickname) {
        validateNickname(nickname);
        this.nickName = nickname;
    }

    public boolean isRegisteredOAuthMember() {
        return nickName != null;
    }

    // 프로필 사진 등록
    public void updateProfileImage(String imageUrl) {
        this.image_url = imageUrl;
    }

    // 사용자 신고횟수 증가
    public void increaseReportCount() {
        this.report_count++;
    }
}
