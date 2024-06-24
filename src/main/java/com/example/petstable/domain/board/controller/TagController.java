package com.example.petstable.domain.board.controller;

import com.example.petstable.domain.board.entity.TagEntity;
import com.example.petstable.domain.board.repository.TagRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;

    @Operation(summary = "몽고디비 테스트")
    @PostMapping
    @SecurityRequirement(name = "JWT")
    public TagEntity createTag(@RequestBody TagEntity tag) {
        return tagRepository.save(tag);
    }
}
