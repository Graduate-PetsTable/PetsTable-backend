package com.example.petstable.service;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.point.entity.PointEntity;
import com.example.petstable.domain.point.repository.PointRepository;
import com.example.petstable.domain.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;


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
                .build();
        memberRepository.save(member);

        PointEntity pointEntity = PointEntity.createPoint(member);
        pointRepository.save(pointEntity);

        // when
        int expected = 10;

        pointService.increasePoints(member.getId());
        int actual = pointService.getPointByMemberId(member.getId()).getPoint();
        int actual2 = pointRepository.findByMemberId(member.getId()).orElseThrow().getPoint();

        // then
        assertThat(actual).isEqualTo(expected);
        assertThat(actual2).isEqualTo(expected);
    }
}
