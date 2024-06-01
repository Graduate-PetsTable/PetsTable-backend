package com.example.petstable.repository;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("SocialType.APPLE, socialId 값으로 회원의 id를 조회")
    void findIdByPlatformAndPlatformId() {
        MemberEntity member = MemberEntity.builder()
                .email("ssg@apple.com")
                .nickName("Seung-9")
                .image_url(null)
                .socialType(SocialType.APPLE)
                .socialId("20191476")
                .build();

        MemberEntity savedMember = memberRepository.save(member);

        Long actual = memberRepository.findIdBySocialTypeAndSocialId(SocialType.APPLE, "20191476").orElseThrow();

        assertThat(actual).isEqualTo(savedMember.getId());
    }
}