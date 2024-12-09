package com.example.petstable.domain.detail.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetailRequest {
    private MultipartFile image; // 이미지
    private String description; // 설명
}
