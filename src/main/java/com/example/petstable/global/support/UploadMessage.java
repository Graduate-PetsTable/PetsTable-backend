package com.example.petstable.global.support;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UploadMessage implements ResponseMessage {

    FILE_UPLOAD_FAIL("이미지 업로드에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_INVALID_EMPTY("빈 파일은 업로드할 수 없습니다", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}