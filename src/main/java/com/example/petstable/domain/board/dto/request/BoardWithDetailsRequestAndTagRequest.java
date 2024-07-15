package com.example.petstable.domain.board.dto.request;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BoardWithDetailsRequestAndTagRequest {

    private String title;
    private List<DetailRequest> details;
    private List<TagRequest> tags;
}
