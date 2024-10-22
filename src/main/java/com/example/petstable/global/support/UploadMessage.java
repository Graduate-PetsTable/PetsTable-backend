package com.example.petstable.global.support;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UploadMessage implements ResponseMessage {

    FILE_UPLOAD_FAIL("이미지 업로드에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAIL("이미지 삭제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    WRONG_IMAGE_URL("잘못된 이미지 URL 입니다.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_TYPE("지원하지 않는 이미지 확장자 입니다", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_SIZE("지원하지 않는 이미지 크기 입니다.", HttpStatus.BAD_REQUEST),
    FILE_INVALID_EMPTY("빈 파일은 업로드할 수 없습니다", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}