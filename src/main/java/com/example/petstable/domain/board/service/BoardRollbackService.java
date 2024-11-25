package com.example.petstable.domain.board.service;

import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

import static com.example.petstable.domain.board.message.BoardMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardRollbackService {
    private final RedisTemplate<String, String> redisTemplateForCluster;

    public void rollbackReipce(RecordId recordId) {
        PendingMessages pendingMessage = redisTemplateForCluster.opsForStream().pending(
                "recipePoint", Consumer.from("recipePointGroup", "instance-1"), Range.unbounded(), 100L
        );
        for (PendingMessage message : pendingMessage) {
            if(message.getId() == recordId){
                redisTemplateForCluster.opsForStream().acknowledge("reipcePoint", "reipcePointGroup", recordId);
                throw new PetsTableException(RECIPE_CREATE_ERROR.getStatus(), RECIPE_CREATE_ERROR.getMessage(), 400);
            }
        }
    }
}