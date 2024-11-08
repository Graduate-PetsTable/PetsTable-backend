package com.example.petstable.domain.board.service;

import com.example.petstable.domain.board.dto.request.TagRequest;
import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.entity.TagEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {
    public List<TagEntity> createTags(List<TagRequest> tags, BoardEntity recipe) {
        return Optional.ofNullable(tags)
                .orElse(Collections.emptyList())
                .stream()
                .map(tagRequest -> TagEntity.create(
                        tagRequest.getTagType(),
                        tagRequest.getTagName(),
                        recipe
                ))
                .toList();
    }
}
