package com.example.petstable.domain.board.dto.response;

import java.util.List;

public record BoardReadAllResponse(

        List<BoardReadWithBookmarkResponse> recipes,
        PageResponse pageResponse
) {
}
