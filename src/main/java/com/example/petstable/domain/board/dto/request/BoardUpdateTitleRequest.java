package com.example.petstable.domain.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardUpdateTitleRequest {

    @Schema(description = "제목", example = "말티즈를 위한 닭죽 만들기 ( 수정본 )")
    private String title;
}
