package com.example.petstable.domain.board.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardUpdateTagRequest {

    private String tagType;
    private String tagName;
}
