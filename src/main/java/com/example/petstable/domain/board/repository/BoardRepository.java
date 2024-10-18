package com.example.petstable.domain.board.repository;

import com.example.petstable.domain.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Long>, BoardCustomRepository {

    @EntityGraph(attributePaths = {"details", "tags"})
    Optional<BoardEntity> findById(Long id);

    Optional<BoardEntity> findByTitle(String title);

    @Query("SELECT b FROM BoardEntity b JOIN FETCH b.details WHERE b.id = :id")
    BoardEntity findByIdWithDetails(@Param("id") Long id);

    @Query("select m.id from BoardEntity m where m.title = :title")
    Long findIdByTitle(@Param("title") String title);
}
