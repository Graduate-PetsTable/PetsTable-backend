package com.example.petstable.domain.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagRequest {

    @Schema(description = "태그 타입", allowableValues = {"기능별", "나이별", "건조별", "크기별", "준비 시간"})
    private String tagType;

    @Schema(description = "태그 이름", allowableValues = {"모질개선", "다이어트", "구강개선", "신장관련", "관절관련", })
    private String tagName;
}
