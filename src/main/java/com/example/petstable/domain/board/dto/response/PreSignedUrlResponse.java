package com.example.petstable.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.net.URL;

@Getter
@Builder
public class PreSignedUrlResponse {
    private String preSignedUrl;
    private String fileUrl;

    public static PreSignedUrlResponse toPreSignedUrlResponse(URL url, String fileUrl) {
        String preSignedUrlStr = url.toString();
        int queryIndex = preSignedUrlStr.indexOf("?");
        String fileUrlOnly = (queryIndex != -1) ? preSignedUrlStr.substring(0, queryIndex) : preSignedUrlStr;
        return PreSignedUrlResponse.builder()
                .preSignedUrl(preSignedUrlStr)
                .fileUrl(fileUrlOnly)  // 쿼리 파라미터를 제외한 URL만 저장
                .build();
    }

}
