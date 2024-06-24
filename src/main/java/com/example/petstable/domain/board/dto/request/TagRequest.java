package com.example.petstable.domain.board.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagRequest {
    private String tagType;
    private String tagName;
}
