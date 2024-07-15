package com.example.petstable.domain.board.dto.response;

import com.example.petstable.domain.board.entity.TagType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagResponse {

    TagType tagType;
    String tagName;
}
