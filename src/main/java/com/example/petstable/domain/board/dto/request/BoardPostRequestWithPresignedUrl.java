package com.example.petstable.domain.board.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardPostRequestWithPresignedUrl {

    @Schema(description = "제목", example = "말티즈를 위한 닭죽 만들기")
    private String title;

    @Schema(description = "썸네일 URI")
    private String thumbnailUrl;

    @ArraySchema(schema = @Schema(implementation = PreSignedUrlAndDescriptionRequest.class, description = "조리 과정"))
    private List<PreSignedUrlAndDescriptionRequest> details;

    @ArraySchema(schema = @Schema(implementation = TagRequest.class, description = "태그 리스트"))
    private List<TagRequest> tags;

    @ArraySchema(schema = @Schema(implementation = IngredientRequest.class, description = "재료 리스트"))
    private List<IngredientRequest> ingredients;
}
