package com.example.petstable.domain.board.dto.request;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BoardPostRequest {

    private String title;
    private List<DescriptionRequest> descriptions;
    private List<TagRequest> tags;
}
