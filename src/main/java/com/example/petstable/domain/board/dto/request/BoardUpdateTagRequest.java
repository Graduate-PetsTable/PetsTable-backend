package com.example.petstable.domain.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardUpdateTagRequest {
    @Schema(description = "태그 타입", type = "string", allowableValues = {"기능별", "나이별", "건조별", "크기별", "준비 시간"}, example = "기능별")
    private String tagType;

    @Schema(description = "태그 이름", type = "string", allowableValues = {"모질개선", "성견", "동결건조", "대형", "30분 이내"}, example = "모질개선")
    private String tagName;
}
