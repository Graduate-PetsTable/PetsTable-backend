package com.example.petstable.domain.board.dto.request;

import com.example.petstable.domain.board.entity.BoardEntity;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RecipeCreateEvent {
    private BoardEntity recipe;
    private List<PreSignedUrlAndDescriptionRequest> details;
    private List<TagRequest> tags;
    private List<IngredientRequest> ingredients;

    public static RecipeCreateEvent of(BoardEntity recipe,
                                       List<PreSignedUrlAndDescriptionRequest> details,
                                       List<TagRequest> tags,
                                       List<IngredientRequest> ingredientRequests) {
        return RecipeCreateEvent.builder()
                .recipe(recipe)
                .details(details)
                .tags(tags)
                .ingredients(ingredientRequests)
                .build();
    }
}
