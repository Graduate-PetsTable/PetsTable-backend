package com.example.petstable.domain.board.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardMessage implements ResponseMessage {

    GET_INFO_SUCCESS("상세 조회 성공", HttpStatus.OK),
    GET_POST_ALL_SUCCESS("레시피 목록 조회 성공", HttpStatus.OK),
    WRITE_SUCCESS("레시피 작성 성공", HttpStatus.OK),
    RECIPE_IS_EMPTY("레시피가 존재하지 않습니다.", HttpStatus.NO_CONTENT),
    POST_NOT_FOUND("해당 레시피를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}
