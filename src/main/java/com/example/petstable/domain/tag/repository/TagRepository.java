package com.example.petstable.domain.tag.repository;

import com.example.petstable.domain.tag.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    @Query("SELECT t FROM TagEntity t WHERE t.post.id = :postId")
    List<TagEntity> findTagsByPostId(@Param("postId") Long postId);
    @Query("SELECT t FROM TagEntity t WHERE t.name = :tagName")
    TagEntity findByName(@Param("tagName") String tagName);
    @Transactional
    void deleteByPostId(Long postId);
}
