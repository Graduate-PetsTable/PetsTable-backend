package com.example.petstable.domain.board.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardWithDetailsRequestAndTagRequest {

    private String title;
    private List<DetailRequest> details;
    private List<TagRequest> tags;
}
