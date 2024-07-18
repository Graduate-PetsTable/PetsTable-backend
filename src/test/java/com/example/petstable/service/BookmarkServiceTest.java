package com.example.petstable.service;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.repository.BoardRepository;
import com.example.petstable.domain.bookmark.entity.BookmarkEntity;
import com.example.petstable.domain.bookmark.repository.BookmarkRepository;
import com.example.petstable.domain.bookmark.service.BookmarkService;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookmarkServiceTest {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @DisplayName("북마크가 등록되어 있지 않을 경우 북마크 등록")
    @Test
    void registerBookmark() {

        // given
        MemberEntity member = MemberEntity.builder()
                .nickName("test")
                .build();

        MemberEntity registerBookmarkMember = MemberEntity.builder()
                .nickName("ssg")
                .build();

        memberRepository.saveAll(List.of(member, registerBookmarkMember));

        BoardEntity post = BoardEntity.builder()
                .title("북마크 테스트")
                .member(member)
                .build();

        boardRepository.save(post);

        // when
        bookmarkService.registerBookmark(registerBookmarkMember.getId(), post.getId());

        Optional<BookmarkEntity> actual = bookmarkRepository.findByMemberIdAndPostId(registerBookmarkMember.getId(), post.getId());

        Optional<BookmarkEntity> eee = bookmarkRepository.findByMemberIdAndPostIdWithMemberAndPost(registerBookmarkMember.getId(), post.getId());
        // then
        assertAll(
                () -> assertThat(actual.get()).isNotNull(),
                () -> assertThat(actual.get().isStatus()).isTrue(),
                () -> assertThat(eee.get().getMember().getNickName()).isEqualTo("ssg"),
                () -> assertThat(eee.get().getPost().getTitle()).isEqualTo("북마크 테스트")
        );
    }

    @DisplayName("북마크가 이미 등록되어 있을 경우 북마크 등록")
    @Test
    void deleteBookmark() {
        // given
        MemberEntity member = MemberEntity.builder()
                .nickName("test")
                .build();

        MemberEntity registerBookmarkMember = MemberEntity.builder()
                .nickName("ssg")
                .build();

        memberRepository.saveAll(List.of(member, registerBookmarkMember));

        BoardEntity post = BoardEntity.builder()
                .title("북마크 테스트")
                .member(member)
                .build();

        boardRepository.save(post);

        // when
        bookmarkService.registerBookmark(registerBookmarkMember.getId(), post.getId()); // 등록
        bookmarkService.registerBookmark(registerBookmarkMember.getId(), post.getId()); // 삭제
        Optional<BookmarkEntity> actual = bookmarkRepository.findByMemberIdAndPostId(registerBookmarkMember.getId(), post.getId());

        // then
        assertThat(actual).isEmpty();
    }
}
