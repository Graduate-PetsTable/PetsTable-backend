package com.example.petstable.service;


import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.repository.BoardRepository;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.report.dto.response.ReportPostResponse;
import com.example.petstable.domain.report.service.ReportService;
import com.example.petstable.global.exception.PetsTableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @DisplayName("게시글을 신고하면 게시글 작성자의 신고 횟수 및 게시글의 신고 횟수가 1회씩 증가한다")
    @Test
    void reportPost() {

        // given
        MemberEntity writer = MemberEntity.builder()
                .nickName("악동")
                .build();

        MemberEntity reporter = MemberEntity.builder()
                .nickName("쿠쿸")
                .build();

        memberRepository.saveAll(List.of(writer, reporter));

        BoardEntity post = BoardEntity.builder()
                .title("테스트")
                .member(writer)
                .build();

        boardRepository.save(post);

        // when
        ReportPostResponse response = reportService.reportPost(reporter.getId(), post.getId(), "inappropriate_content");
        MemberEntity actualMember = memberRepository.findById(writer.getId()).orElseThrow();
        BoardEntity actualPost = boardRepository.findById(post.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(response.getPostReportCount()).isEqualTo(1),
                () -> assertThat(actualMember.getReport_count()).isEqualTo(1),
                () -> assertThat(actualPost.getReportCount()).isEqualTo(1)
        );
    }

    @DisplayName("동일한 게시글을 2번 이상 신고할 경우 예외를 반환한다.")
    @Test
    void reportDuplicatePost() {

        // given
        MemberEntity writer = MemberEntity.builder()
                .nickName("악동")
                .build();

        MemberEntity reporter = MemberEntity.builder()
                .nickName("쿠쿸")
                .build();

        memberRepository.saveAll(List.of(writer, reporter));

        BoardEntity post = BoardEntity.builder()
                .title("테스트")
                .member(writer)
                .build();

        boardRepository.save(post);

        // when
        reportService.reportPost(reporter.getId(), post.getId(), "inappropriate_content");

        // then
        assertThatThrownBy(
                () -> reportService.reportPost(reporter.getId(), post.getId(), "insult"))
                .isInstanceOf(PetsTableException.class);
    }

    @DisplayName("자신이 작성한 게시글을 신고할 경우 예외를 반환한다.")
    @Test
    void reportOwnPost() {
        MemberEntity member = MemberEntity.builder()
                .nickName("악동")
                .build();

        memberRepository.save(member);

        BoardEntity post = BoardEntity.builder()
                .title("테스트")
                .member(member)
                .build();

        boardRepository.save(post);

        assertThatThrownBy(
                () -> reportService.reportPost(member.getId(), post.getId(), "insult"))
                .isInstanceOf(PetsTableException.class);
    }

    @DisplayName("5번 이상 신고된 게시글은 삭제된다.")
    @Test
    void deletePostReportCountThresholdExceeded() {

        // given
        MemberEntity writer = MemberEntity.builder()
                .nickName("악동")
                .build();

        MemberEntity reporter1 = MemberEntity.builder()
                .nickName("신고자1")
                .build();

        MemberEntity reporter2 = MemberEntity.builder()
                .nickName("신고자2")
                .build();

        MemberEntity reporter3 = MemberEntity.builder()
                .nickName("신고자3")
                .build();

        MemberEntity reporter4 = MemberEntity.builder()
                .nickName("신고자4")
                .build();

        MemberEntity reporter5 = MemberEntity.builder()
                .nickName("신고자5")
                .build();


        memberRepository.saveAll(List.of(writer, reporter1, reporter2, reporter3, reporter4, reporter5));

        BoardEntity post = BoardEntity.builder()
                .title("테스트")
                .member(writer)
                .build();

        boardRepository.save(post);

        // when
        reportService.reportPost(reporter1.getId(), post.getId(), "insult");
        reportService.reportPost(reporter2.getId(), post.getId(), "insult");
        reportService.reportPost(reporter3.getId(), post.getId(), "insult");
        reportService.reportPost(reporter4.getId(), post.getId(), "insult");
        reportService.reportPost(reporter5.getId(), post.getId(), "insult");

        Long id = post.getId();

        MemberEntity actualMember = memberRepository.findById(writer.getId()).orElseThrow();
        BoardEntity actualPost = boardRepository.findById(id).orElseThrow();

        // then
        assertAll(
                () -> assertThat(actualMember.getReport_count()).isEqualTo(5),
                () -> assertThat(actualPost.getTitle()).isNull()
        );

    }
}
