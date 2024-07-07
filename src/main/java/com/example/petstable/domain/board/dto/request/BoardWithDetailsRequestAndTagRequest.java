package com.example.petstable.domain.board.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
public class BoardWithDetailsRequestAndTagRequest {

    private String title;
    private MultipartFile thumbnail_url;
    private List<DetailRequest> details;
    private List<TagRequest> tags;
}
