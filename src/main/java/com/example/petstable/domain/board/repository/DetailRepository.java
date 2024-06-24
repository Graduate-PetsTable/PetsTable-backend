package com.example.petstable.domain.board.repository;

import com.example.petstable.domain.board.entity.DetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetailRepository extends JpaRepository<DetailEntity, Long> {

    @Query("SELECT d FROM DetailEntity d WHERE d.post.id = :postId")
    List<DetailEntity> findDetailsByPostId(@Param("postId") Long postId);
}
