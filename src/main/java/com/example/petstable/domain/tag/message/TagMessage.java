package com.example.petstable.domain.tag.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TagMessage implements ResponseMessage {
    INVALID_TAG("태그 타입 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    TAG_NOT_FOUND("해당 태그가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}
