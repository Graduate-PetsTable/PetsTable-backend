package com.example.petstable.domain.bookmark.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BookmarkMessage implements ResponseMessage {

    SUCCESS_REGISTER_BOOKMARK("북마크가 등록되었습니다..", HttpStatus.OK),
    SUCCESS_GET_MY_BOOKMARK("내 북마크를 모두 조회하였습니다.", HttpStatus.OK),
    EMPTY_MY_BOOKMARK("내 북마크가 존재하지 않습니다.", HttpStatus.OK),
    SUCCESS_DELETE_BOOKMARK("북마크가 삭제되었습니다.", HttpStatus.OK);

    private final String message;
    private final HttpStatus status;
}
