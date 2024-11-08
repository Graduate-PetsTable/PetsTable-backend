package com.example.petstable.domain.board.service;

import com.example.petstable.domain.board.dto.request.PreSignedUrlAndDescriptionRequest;
import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.entity.DetailEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetailService {
    public List<DetailEntity> createDetails(List<PreSignedUrlAndDescriptionRequest> details, BoardEntity recipe) {
        return Optional.ofNullable(details)
                .orElse(Collections.emptyList())
                .stream()
                .map(detailRequest -> DetailEntity.create(
                        detailRequest.getImage(),
                        detailRequest.getDescription(),
                        recipe
                ))
                .toList();
    }
}
