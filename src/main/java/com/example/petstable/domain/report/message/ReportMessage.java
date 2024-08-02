package com.example.petstable.domain.report.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportMessage implements ResponseMessage {

    REPORT_POST_SUCCESS("해당 게시글 신고 완료", HttpStatus.OK),
    SELF_REPORT_NOT_ALLOWED("내 게시글을 신고할 수 없습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_REPORT("이미 신고한 게시글입니다.", HttpStatus.BAD_REQUEST),
    INVALID_REASON("해당 신고 사유가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REPORT_POST("이미 신고한 게시글입니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
