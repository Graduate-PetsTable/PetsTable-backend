package com.example.petstable.domain.board.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BoardPostRequest {

    @Schema(description = "제목", example = "말티즈를 위한 닭죽 만들기")
    private String title;

    @ArraySchema(schema = @Schema(implementation = DescriptionRequest.class, description = "설명 리스트"))
    private List<DescriptionRequest> descriptions;

    @ArraySchema(schema = @Schema(implementation = TagRequest.class, description = "태그 리스트"))
    private List<TagRequest> tags;

    @ArraySchema(schema = @Schema(implementation = IngredientRequest.class, description = "재료 리스트"))
    private List<IngredientRequest> ingredients;
}
