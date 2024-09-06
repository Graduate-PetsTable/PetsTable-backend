package com.example.petstable.domain.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BoardPostRequest {

    @Schema(description = "제목", example = "말티즈를 위한 닭죽 만들기")
    private String title;

    @Schema(description = "설명 리스트", implementation = DescriptionRequest.class)
    private List<DescriptionRequest> descriptions;

    @Schema(description = "태그 리스트", implementation = TagRequest.class)
    private List<TagRequest> tags;

    @Schema(description = "재료 리스트", implementation = IngredientRequest.class)
    private List<IngredientRequest> ingredients;
}
