package com.example.petstable.domain.tag.dto.response;

import com.example.petstable.domain.tag.entity.TagType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagResponse {
    TagType tagType;
    String tagName;
}
