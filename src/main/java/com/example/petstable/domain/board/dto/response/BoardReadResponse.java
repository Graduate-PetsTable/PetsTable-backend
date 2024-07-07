package com.example.petstable.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardReadResponse {

    private String title; // 제목
    private String imageUrl; // 썸네일 이미지
    private List<String> tagName; // 태그 이름 목록
}
