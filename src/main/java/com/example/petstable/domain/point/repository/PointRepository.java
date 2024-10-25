package com.example.petstable.domain.point.repository;

import com.example.petstable.domain.point.entity.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<PointEntity, Long> {

    @Query("select p from PointEntity p where p.member.id = :memberId")
    List<PointEntity> findByMemberId(@Param("memberId") Long memberId);

    PointEntity findFirstByMemberIdOrderByCreatedTimeDesc(Long memberId);
}
