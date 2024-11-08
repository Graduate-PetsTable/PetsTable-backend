package com.example.petstable.domain.board.dto.response;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.global.config.AmazonConfig;
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

    public static BoardDetailReadResponse from(BoardEntity boardEntity, boolean status, AmazonConfig amazonConfig) {
        return BoardDetailReadResponse.builder()
                .id(boardEntity.getId())
                .title(boardEntity.getTitle())
                .thumbnail(updateThumbnailUrlIfNeeded(boardEntity.getThumbnail_url() + "?size=thumbnail", amazonConfig))
                .author(boardEntity.getMember().getNickName())
                .viewCount(boardEntity.getView_count())
                .createdAt(boardEntity.getCreatedTime())
                .bookmarkStatus(status)
                .details(boardEntity.getDetails().stream()
                        .map(detail -> DetailResponse.builder()
                                .image_url(updateThumbnailUrlIfNeeded(detail.getImage_url() + "?size=step", amazonConfig))
                                .description(detail.getDescription())
                                .build())
                        .toList())
                .tags(boardEntity.getTags().stream()
                        .map(tag -> TagResponse.builder()
                                .tagType(tag.getType())
                                .tagName(tag.getName())
                                .build())
                        .collect(Collectors.toList()))
                .ingredients(boardEntity.getIngredients().stream()
                        .map(ingredient -> IngredientResponse.builder()
                                .name(ingredient.getName())
                                .weight(ingredient.getWeight())
                                .build())
                        .toList())
                .build();
    }

    // S3 URL을 CloudFront URL로 변환
    private static String updateThumbnailUrlIfNeeded(String url, AmazonConfig amazonConfig) {
        if (url.startsWith(amazonConfig.getS3Uri())) {
            return url.replace(amazonConfig.getS3Uri(), amazonConfig.getCloudfrontUri());
        }
        return url;
    }
}
