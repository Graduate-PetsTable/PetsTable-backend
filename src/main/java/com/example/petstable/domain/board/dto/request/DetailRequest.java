package com.example.petstable.domain.board.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class DetailRequest {

    private MultipartFile image_url; // 이미지
    private String description; // 설명
}
