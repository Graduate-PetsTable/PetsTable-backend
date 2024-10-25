package com.example.petstable.service;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.point.dto.response.PointResponse;
import com.example.petstable.domain.point.entity.PointEntity;
import com.example.petstable.domain.point.repository.PointRepository;
import com.example.petstable.domain.point.service.PointService;
import com.example.petstable.global.exception.PetsTableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PointServiceTest {

    @Autowired
    private PointService pointService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PointRepository pointRepository;

    @Test
    @DisplayName("포인트가 증가 및 조회에 성공한다.")
    void increasePointTest() {
        // given
        MemberEntity member = MemberEntity.builder()
                .email("test@apple.com")
                .nickName("test")
                .socialType(SocialType.APPLE)
                .points(new ArrayList<>())
                .build();
        memberRepository.save(member);

        // when
        int expected = 100;

        pointService.increasePoints(member, 100, "회원가입");
        int actual = pointService.getPointHistoryByMemberId(member.getId()).getPoint();
        int actual2 = pointRepository.findByMemberId(member.getId()).orElseThrow().getPoint();

        // then
        assertThat(actual).isEqualTo(expected);
        assertThat(actual2).isEqualTo(expected);
    }

    @Test
    @DisplayName("현재 포인트보다 차감될 포인트가 더 클 경우 예외를 반환하며 금액이 차감되어선 안 된다.")
    void subtractPointTest() {
        // given
        MemberEntity member = MemberEntity.builder()
                .email("test142@apple.com")
                .nickName("test231")
                .socialType(SocialType.APPLE)
                .points(new ArrayList<>())
                .build();
        memberRepository.save(member);

        pointService.increasePoints(member, 50, "테스트");

        // when & then
        String expectedMessage = "잔액이 부족하여 포인트를 사용할 수 없습니다.";
        PetsTableException actualMessage = assertThrows(PetsTableException.class, () -> {
            pointService.subtractPoints(member, 100, "포인트 사용");
        });
        assertTrue(actualMessage.getMessage().contains(expectedMessage));

        PointEntity actual = pointRepository.findFirstByMemberIdOrderByCreatedTimeDesc(member.getId());
        assertNotNull(actual);
        assertThat(actual.getPoint()).isEqualTo(50); // 포인트가 50으로 유지되어야 함

    }

    @Test
    @DisplayName("포인트 사용 시 포인트가 차감된다.")
    void subtractPointExceptionTest() {
        // given
        MemberEntity member = MemberEntity.builder()
                .email("test221@apple.com")
                .nickName("test312")
                .socialType(SocialType.APPLE)
                .points(new ArrayList<>())
                .build();
        memberRepository.save(member);

        // when
        pointService.increasePoints(member, 100, "회원가입");
        pointService.subtractPoints(member, 50, "테스트");

        // then
        int expected = 50;
        int actual = pointRepository.findFirstByMemberIdOrderByCreatedTimeDesc(member.getId()).getBalance();
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @DisplayName("현재 잔액 조회에 성공한다.")
    void getPointBalanceTest() {
        // given
        MemberEntity member = MemberEntity.builder()
                .email("test552@apple.com")
                .nickName("test930")
                .socialType(SocialType.APPLE)
                .points(new ArrayList<>())
                .build();
        memberRepository.save(member);

        // when
        pointService.increasePoints(member, 100, "회원가입");
        int expected = 100;
        int actual = pointService.getPointBalance(member.getId()).getPoint();
        // then
        assertThat(expected).isEqualTo(actual);
    }
}
