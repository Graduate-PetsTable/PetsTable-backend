package com.example.petstable.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DetailResponse {

    private String title;
    private int view_count;
    private String image_url;
    private String description;
}
