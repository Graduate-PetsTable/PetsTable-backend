package com.example.petstable.domain.board.dto.response;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.entity.TagEntity;
import com.example.petstable.domain.board.entity.TagType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BoardDetailReadResponse {

    private String title;
    private List<DetailResponse> details;
    private List<TagResponse> tags;

    public static BoardDetailReadResponse from(BoardEntity boardEntity) {

        List<DetailResponse> details = boardEntity.getDescription().stream()
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
                .details(details)
                .tags(tags)
                .build();
    }
}
