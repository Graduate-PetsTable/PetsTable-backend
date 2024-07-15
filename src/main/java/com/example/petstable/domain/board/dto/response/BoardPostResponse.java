package com.example.petstable.domain.board.dto.response;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BoardPostResponse {

    private String title;
    private List<DetailPostResponse> details;
    private List<TagResponse> tags;
}
