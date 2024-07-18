package com.example.petstable.domain.board.dto.response;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.entity.TagEntity;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardReadWithBookmarkResponse {

    private Long id;
    private String title; // 제목
    private String imageUrl; // 썸네일 이미지
    private boolean bookmarkStatus;
    private List<String> tagName; // 태그 이름 목록

    public BoardReadWithBookmarkResponse(BoardEntity post, boolean status) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.imageUrl = post.getThumbnail_url();
        this.bookmarkStatus = status;
        this.tagName = post.getTags()
                .stream()
                .map(TagEntity::getName)
                .toList();
    }
}
