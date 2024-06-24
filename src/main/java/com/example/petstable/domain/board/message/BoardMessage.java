package com.example.petstable.domain.board.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardMessage implements ResponseMessage {

    GET_INFO_SUCCESS("상세 조회 성공", HttpStatus.OK),
    WRITE_SUCCESS("게시글 작성 성공", HttpStatus.OK),
    POST_NOT_FOUND("해당 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}
