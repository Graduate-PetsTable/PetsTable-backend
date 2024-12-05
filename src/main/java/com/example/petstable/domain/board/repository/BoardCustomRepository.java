package com.example.petstable.domain.board.repository;

import com.example.petstable.domain.board.dto.request.BoardFilteringRequest;
import com.example.petstable.domain.board.dto.response.BoardReadResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardCustomRepository {

    List<BoardReadResponse> findRecipesByQueryDslWithTitleAndContent(BoardFilteringRequest filteringRequest, Long memberId, Pageable pageable);
    List<BoardReadResponse> findRecipesByQueryDslWithTagAndIngredients(BoardFilteringRequest filteringRequest, Long memberId, Pageable pageable);
    void addViewCntFromRedis(Long postId, int addCnt);
}
