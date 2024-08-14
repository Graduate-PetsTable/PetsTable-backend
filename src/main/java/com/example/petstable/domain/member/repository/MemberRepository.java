package com.example.petstable.domain.member.repository;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByEmail(String email);

    Optional<MemberEntity> findByNickName(String nickName);

    Optional<MemberEntity> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    @Query("select m.id from MemberEntity m where m.socialType = :socialType and m.socialId = :socialId")
    Optional<Long> findIdBySocialTypeAndSocialId(@Param("socialType") SocialType socialType, @Param("socialId") String socialId);

    @Query("select m.fcmToken from MemberEntity m where m.id = :memberId")
    Optional<String> findFcmTokenById(@Param("memberId") Long memberId);
}
