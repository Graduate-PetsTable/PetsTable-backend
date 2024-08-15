package com.example.petstable.domain.board.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class DetailUpdateRequest {

    private String description; // 설명
}
