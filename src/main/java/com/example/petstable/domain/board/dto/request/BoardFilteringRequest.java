package com.example.petstable.domain.board.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardFilteringRequest {
    @Schema(description = "제목", example = "수제 간식")
    private String keyword; // 제목 혹은 내용

    @ArraySchema(schema = @Schema(description = "태그 이름"))
    private List<String> tagNames; // 태그

    @ArraySchema(schema = @Schema(description = "재료 이름"))
    private List<String> ingredients; // 재료
}
