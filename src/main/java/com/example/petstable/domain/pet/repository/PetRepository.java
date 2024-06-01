package com.example.petstable.domain.pet.repository;

import com.example.petstable.domain.pet.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<PetEntity, Long> {

    Optional<PetEntity> findByName(String name);
    List<PetEntity> findByMemberId(Long id);

    @Query("select m from PetEntity m where m.id = :id and m.member.id = :memberId")
    Optional<PetEntity> findByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);
}
