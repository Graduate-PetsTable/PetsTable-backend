package com.example.petstable.domain.detail.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class DetailUpdateRequest {
    @Schema(description = "수정된 내용", example = "1단계: 물 1L를 끓인다")
    private String description; // 설명
}
