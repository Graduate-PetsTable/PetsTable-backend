package com.example.petstable.domain.board.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardResponse {
    int count;
    List<BoardReadResponse> recipes;
}
