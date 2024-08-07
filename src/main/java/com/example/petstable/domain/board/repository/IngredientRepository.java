package com.example.petstable.domain.board.repository;

import com.example.petstable.domain.board.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {

    @Query("SELECT i FROM IngredientEntity i WHERE i.post.id = :postId")
    List<IngredientEntity> findIngredientEntityByPostId(@Param("postId") Long postId);
}
