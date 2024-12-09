package com.example.petstable.global.scheduler;

import com.example.petstable.domain.board.service.RecipeViewCntService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewCountScheduler {
    private final RecipeViewCntService recipeViewCntService;

    @Scheduled(cron = "0 0/3 * * * ?")
    public void syncViewCounts() {
        recipeViewCntService.syncViewCountsFromRedis();
    }
}
