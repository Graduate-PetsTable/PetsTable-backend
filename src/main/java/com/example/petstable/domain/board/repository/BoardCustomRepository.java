package com.example.petstable.domain.board.repository;

import com.example.petstable.domain.board.dto.request.BoardFilteringRequest;
import com.example.petstable.domain.board.dto.response.BoardReadWithBookmarkResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardCustomRepository {

    List<BoardReadWithBookmarkResponse> findRecipesByQueryDslWithTitleAndContent(BoardFilteringRequest filteringRequest, Long memberId, Pageable pageable);
    List<BoardReadWithBookmarkResponse> findRecipesByQueryDslWithTagAndIngredients(BoardFilteringRequest filteringRequest, Long memberId, Pageable pageable);
}
