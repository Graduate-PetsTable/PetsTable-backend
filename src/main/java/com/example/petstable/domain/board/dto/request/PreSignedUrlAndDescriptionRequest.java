package com.example.petstable.domain.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreSignedUrlAndDescriptionRequest {
    @Schema(description = "과정 이미지 URI", example = "")
    private String image;
    @Schema(description = "설명", example = "1단계 : 물 500mL 를 넣고 끓인다.")
    private String description;
}
