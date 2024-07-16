package com.example.petstable.domain.bookmark.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BookmarkMessage implements ResponseMessage {

    SUCCESS_REGISTER_BOOKMARK("북마크가 등록되었습니다..", HttpStatus.OK),
    SUCCESS_DELETE_BOOKMARK("북마크가 삭제되었습니다.", HttpStatus.OK);

    private final String message;
    private final HttpStatus status;
}
