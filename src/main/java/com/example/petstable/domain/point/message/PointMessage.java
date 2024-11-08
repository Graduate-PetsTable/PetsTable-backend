package com.example.petstable.domain.point.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PointMessage implements ResponseMessage {

    SUCCESS_GET_POINT("포인트 조회에 성공하였습니다..", HttpStatus.OK),
    INSUFFICIENT_BALANCE("잔액이 부족하여 포인트를 사용할 수 없습니다.", HttpStatus.BAD_REQUEST),
    POINT_CREATE_ERROR("포인트 생성에 실패했습니다", HttpStatus.BAD_REQUEST),
    INVALID_TRANSACTION_TYPE("잘못된 포인트 거래 타입 검색입니다.", HttpStatus.UNAUTHORIZED),
    POINT_NOT_FOUND("해당 회원의 포인트 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}
