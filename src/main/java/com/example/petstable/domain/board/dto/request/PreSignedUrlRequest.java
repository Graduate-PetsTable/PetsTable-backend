package com.example.petstable.domain.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreSignedUrlRequest {
    @Schema(description = "저장 위치", example = "recipe")
    private String prefix;

    @Schema(description = "이미지 이름", example = "example.jpg")
    private String fileName;
}
