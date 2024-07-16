package com.example.petstable.domain.bookmark.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkRegisterResponse {

    private Long memberId;
    private Long postId;
    private boolean status;
}
