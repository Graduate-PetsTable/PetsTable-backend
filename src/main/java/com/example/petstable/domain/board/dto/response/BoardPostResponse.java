package com.example.petstable.domain.board.dto.response;

import com.example.petstable.domain.board.dto.request.DetailRequest;
import com.example.petstable.domain.board.dto.request.TagRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class BoardPostResponse {

    private String title;
    private List<DetailRequest> details;
    private List<TagRequest> tags;
}
