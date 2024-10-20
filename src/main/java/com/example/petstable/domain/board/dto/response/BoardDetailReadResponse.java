package com.example.petstable.domain.board.dto.response;

import com.example.petstable.domain.board.entity.BoardEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BoardDetailReadResponse {

    private Long id;
    private String title;
    private String author;
    private String thumbnail;
    private int viewCount;
    private LocalDateTime createdAt;
    private boolean bookmarkStatus;
    private List<DetailResponse> details;
    private List<TagResponse> tags;
    private List<IngredientResponse> ingredients;

    public static BoardDetailReadResponse from(BoardEntity boardEntity, boolean status) {

        List<DetailResponse> details = boardEntity.getDetails().stream()
                .map(detail -> DetailResponse.builder()
                        .image_url(detail.getImage_url())
                        .description(detail.getDescription())
                        .build())
                .toList();

        List<TagResponse> tags = boardEntity.getTags().stream()
                .map(tag -> TagResponse.builder()
                        .tagType(tag.getType())
                        .tagName(tag.getName())
                        .build())
                .collect(Collectors.toList());

        List<IngredientResponse> ingredients = boardEntity.getIngredients().stream()
                .map(ingredient -> IngredientResponse.builder()
                        .name(ingredient.getName())
                        .weight(ingredient.getWeight())
                        .build())
                .toList();

        return BoardDetailReadResponse.builder()
                .id(boardEntity.getId())
                .title(boardEntity.getTitle())
                .thumbnail(boardEntity.getThumbnail_url())
                .author(boardEntity.getMember().getNickName())
                .viewCount(boardEntity.getView_count())
                .createdAt(boardEntity.getCreatedTime())
                .bookmarkStatus(status)
                .details(details)
                .tags(tags)
                .ingredients(ingredients)
                .build();
    }
}
