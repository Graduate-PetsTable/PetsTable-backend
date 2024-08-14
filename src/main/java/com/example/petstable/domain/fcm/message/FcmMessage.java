package com.example.petstable.domain.fcm.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FcmMessage implements ResponseMessage {

    FCM_INIT_EXCEPTION("FCM 설정에 실패하였습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_FCM_TOKEN("FCM Token이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    SUCCESS_SEND_MESSAGE("메세지 전송에 성공하였습니다.", HttpStatus.OK),
    FAIL_SEND_MESSAGE("메세지 전송에 실패하였습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
