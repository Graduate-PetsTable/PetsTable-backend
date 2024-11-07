package com.example.petstable.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.net.URL;

@Getter
@Builder
public class PreSignedUrlResponse {
    private String preSignedUrl;

    public static PreSignedUrlResponse toPreSignedUrlResponse(URL url) {
        return PreSignedUrlResponse.builder()
                .preSignedUrl(url.toString())
                .build();
    }
}
