package com.example.petstable.global.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OAuthLoginMessage implements ResponseMessage {

    BAD_REQUEST_PUBLIC_KEY("로그인 중 Public Key 생성에 문제가 발생했습니다..", HttpStatus.BAD_REQUEST),
    INVALID_ID_TOKEN("Id Token 값이 유효하지 않습니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_ID_TOKEN("Id Token 이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_CLAIMS("Apple OAuth Claims 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_BEARER("로그인이 필요한 서비스입니다.", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR("암호화 과정 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;
}