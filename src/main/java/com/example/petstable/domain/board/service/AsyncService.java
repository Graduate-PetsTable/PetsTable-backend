package com.example.petstable.domain.board.service;

import com.example.petstable.domain.board.dto.request.*;
import com.example.petstable.domain.board.dto.response.RecipeWithDetailsAndTagsDto;
import com.example.petstable.domain.detail.entity.DetailEntity;
import com.example.petstable.domain.detail.service.DetailService;
import com.example.petstable.domain.ingredient.entity.IngredientEntity;
import com.example.petstable.domain.ingredient.service.IngredientService;
import com.example.petstable.domain.tag.entity.TagEntity;
import com.example.petstable.domain.point.dto.request.PointMessage;
import com.example.petstable.domain.point.dto.request.PointRequest;
import com.example.petstable.domain.tag.service.TagService;
import com.example.petstable.global.exception.PetsTableException;
import jakarta.persistence.QueryTimeoutException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.petstable.domain.member.message.AuthMessage.REDIS_CONNECTION_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncService {
    private final DetailService detailService;
    private final TagService tagService;
    private final IngredientService ingredientService;
    private final RedisTemplate<String, String> redisTemplateForCluster;

    @Transactional
    public RecordId publishEventMemberPoint(Long memberId, PointRequest request) {
        try {
            PointMessage pointMessage = PointMessage.of(memberId, request);
            return redisTemplateForCluster.opsForStream().add("recipePoint", pointMessage.toMap());
        } catch (QueryTimeoutException e) {
            log.error("Redis command timed out for userId: {} - Retrying...", memberId, e);
            throw new PetsTableException(REDIS_CONNECTION_ERROR.getStatus(), REDIS_CONNECTION_ERROR.getMessage(), 500);
        } catch (Exception e) {
            log.error("Unexpected error while publishing point event for userId: {}", memberId, e);
            throw new PetsTableException(REDIS_CONNECTION_ERROR.getStatus(), REDIS_CONNECTION_ERROR.getMessage(), 500);
        }
    }

    @Transactional
    public RecipeWithDetailsAndTagsDto runAsyncTasks(RecipeCreateEvent event) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<List<DetailEntity>> details = CompletableFuture.supplyAsync(() -> detailService.createDetails(event.getDetails(), event.getRecipe()), executor);
            CompletableFuture<List<TagEntity>> tags = CompletableFuture.supplyAsync(() -> tagService.createTags(event.getTags(), event.getRecipe()), executor);
            CompletableFuture<List<IngredientEntity>> ingredients = CompletableFuture.supplyAsync(() -> ingredientService.createIngredients(event.getIngredients(), event.getRecipe()), executor);
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(details, tags, ingredients);
            allFutures.join();
            return RecipeWithDetailsAndTagsDto.of(details.join(), tags.join(), ingredients.join());
        }
    }
}
