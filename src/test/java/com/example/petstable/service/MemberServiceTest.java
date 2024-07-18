package com.example.petstable.service;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.repository.BoardRepository;
import com.example.petstable.domain.bookmark.repository.BookmarkRepository;
import com.example.petstable.domain.bookmark.service.BookmarkService;
import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.dto.response.BookmarkMyList;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.global.exception.PetsTableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private BoardRepository boardRepository;

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

    @DisplayName("내 북마크 목록 조회에 성공하낟.")
    @Test
    void findMyBookmarkList() {

        // given
        MemberEntity member = MemberEntity.builder()
                .nickName("test")
                .build();

        MemberEntity registerBookmarkMember = MemberEntity.builder()
                .nickName("ssg")
                .build();

        memberRepository.saveAll(List.of(member, registerBookmarkMember));

        BoardEntity post1 = BoardEntity.builder()
                .title("꿀밤")
                .member(member)
                .build();

        BoardEntity post2 = BoardEntity.builder()
                .title("단밤")
                .member(member)
                .build();

        BoardEntity post3 = BoardEntity.builder()
                .title("맛밤")
                .member(member)
                .build();

        BoardEntity post4 = BoardEntity.builder()
                .title("굿밤")
                .member(registerBookmarkMember)
                .build();

        boardRepository.save(post1);
        boardRepository.save(post2);
        boardRepository.save(post3);
        boardRepository.save(post4);

        // when
        bookmarkService.registerBookmark(registerBookmarkMember.getId(), post1.getId()); // 등록
        bookmarkService.registerBookmark(registerBookmarkMember.getId(), post2.getId()); // 등록
        bookmarkService.registerBookmark(registerBookmarkMember.getId(), post4.getId()); // 등록

        BookmarkMyList actual = memberService.findMyBookmarkList(registerBookmarkMember.getId());

        // then
        assertThat(actual.getRecipes().size()).isEqualTo(3);
    }
}
