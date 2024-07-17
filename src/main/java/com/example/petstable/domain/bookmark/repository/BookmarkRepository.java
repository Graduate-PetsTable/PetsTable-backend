package com.example.petstable.domain.bookmark.repository;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.bookmark.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {

    @Query("SELECT bm FROM BookmarkEntity bm WHERE bm.member.id = :memberId AND bm.post.id = :postId")
    Optional<BookmarkEntity> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT b FROM BookmarkEntity b JOIN FETCH b.member JOIN FETCH b.post WHERE b.member.id = :memberId AND b.post.id = :boardId")
    Optional<BookmarkEntity> findByMemberIdAndPostIdWithMemberAndPost(@Param("memberId") Long memberId, @Param("boardId") Long boardId);

    @Query("SELECT b.post FROM BookmarkEntity b WHERE b.member.id = :memberId")
    List<BoardEntity> findBookmarkedPostsByMemberId(@Param("memberId") Long memberId);
}
