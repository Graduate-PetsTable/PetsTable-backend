package com.example.petstable.domain.board.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BoardWithDetailsAndTagsAndIngredients {

    private String title;
    private MultipartFile thumbnail;
    private List<DetailRequest> details;
    private List<TagRequest> tags;
    private List<IngredientRequest> ingredients;
}
