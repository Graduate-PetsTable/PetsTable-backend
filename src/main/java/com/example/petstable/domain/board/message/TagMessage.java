package com.example.petstable.domain.board.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TagMessage implements ResponseMessage {

    INVALID_TAG("태그 타입 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
