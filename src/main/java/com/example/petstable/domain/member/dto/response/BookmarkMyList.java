package com.example.petstable.domain.member.dto.response;

import com.example.petstable.domain.board.dto.response.BoardReadResponse;
import com.example.petstable.domain.board.entity.BoardEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BookmarkMyList {

    private boolean status;
    List<BoardReadResponse> recipes;

    public static BookmarkMyList createBookmarkMyRegisterResponse(List<BoardEntity> recipes) {

        List<BoardReadResponse> myBookmarkRecipes = recipes
                .stream()
                .map(BoardReadResponse::new)
                .toList();

        return BookmarkMyList.builder()
                .status(true)
                .recipes(myBookmarkRecipes)
                .build();
    }
}
