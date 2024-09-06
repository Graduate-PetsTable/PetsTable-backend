package com.example.petstable.domain.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DescriptionRequest {

    @Schema(description = "설명", example = "1단계 : 물 500mL 를 넣고 끓인다.")
    private String description;
}
