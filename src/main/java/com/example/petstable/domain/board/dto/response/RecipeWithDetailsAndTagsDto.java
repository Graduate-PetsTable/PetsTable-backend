package com.example.petstable.domain.board.dto.response;

import com.example.petstable.domain.board.entity.DetailEntity;
import com.example.petstable.domain.board.entity.IngredientEntity;
import com.example.petstable.domain.board.entity.TagEntity;
import lombok.Builder;

import java.util.List;

@Builder
public record RecipeWithDetailsAndTagsDto(List<DetailEntity> details, List<TagEntity> tags, List<IngredientEntity> ingredients) {
    public static RecipeWithDetailsAndTagsDto of(List<DetailEntity> details, List<TagEntity> tags, List<IngredientEntity> ingredients) {
        return RecipeWithDetailsAndTagsDto.builder().details(details).tags(tags).ingredients(ingredients).build();
    }
}