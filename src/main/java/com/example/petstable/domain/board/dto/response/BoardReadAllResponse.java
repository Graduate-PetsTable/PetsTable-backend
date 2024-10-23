package com.example.petstable.domain.board.dto.response;

import java.util.List;

public record BoardReadAllResponse(

        List<BoardReadResponse> recipes,
        PageResponse pageResponse
) {
}
