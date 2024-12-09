package com.example.petstable.domain.board.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BoardPostResponse {
    private Long id;
    private String title;
}
