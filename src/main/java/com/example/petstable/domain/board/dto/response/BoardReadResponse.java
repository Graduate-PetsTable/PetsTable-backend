package com.example.petstable.domain.board.dto.response;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.ingredient.entity.IngredientEntity;
import com.example.petstable.domain.tag.entity.TagEntity;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardReadResponse {
    private Long id;
    private String title; // 제목
    private String imageUrl; // 썸네일 이미지
    private String author; // 작성자
    private boolean bookmarkStatus;
    private List<String> tagName; // 태그 이름 목록
    private List<String> ingredient; // 재료 이름 목록

    public BoardReadResponse(BoardEntity post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.imageUrl = post.getThumbnail_url() + "?size=preview";
        this.author = post.getMember().getNickName();
        if (post.getTags() != null) {
            this.tagName = post.getTags()
                    .stream()
                    .map(TagEntity::getName)
                    .toList();
        }
        if (post.getIngredients() != null) {
            this.ingredient =post.getIngredients()
                    .stream()
                    .map(IngredientEntity::getName)
                    .toList();
        }
    }

    public BoardReadResponse(BoardEntity post, boolean status) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.imageUrl = post.getThumbnail_url() + "?size=preview";
        this.bookmarkStatus = status;
        this.author = getAuthor();
        if (post.getTags() != null) {
            this.tagName = post.getTags()
                    .stream()
                    .map(TagEntity::getName)
                    .toList();
        }
        if (post.getIngredients() != null) {
            this.ingredient =post.getIngredients()
                    .stream()
                    .map(IngredientEntity::getName)
                    .toList();
        }
    }
}
