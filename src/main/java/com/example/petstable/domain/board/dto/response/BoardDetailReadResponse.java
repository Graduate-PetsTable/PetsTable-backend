package com.example.petstable.domain.board.dto.response;

import com.example.petstable.domain.board.entity.BoardEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BoardDetailReadResponse {

    private String title;
    private int viewCount;
    private List<DetailResponse> details;
    private List<TagResponse> tags;

    public static BoardDetailReadResponse from(BoardEntity boardEntity) {

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

        return BoardDetailReadResponse.builder()
                .title(boardEntity.getTitle())
                .viewCount(boardEntity.getView_count())
                .details(details)
                .tags(tags)
                .build();
    }
}
