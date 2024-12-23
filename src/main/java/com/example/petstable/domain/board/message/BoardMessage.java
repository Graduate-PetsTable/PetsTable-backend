package com.example.petstable.domain.board.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardMessage implements ResponseMessage {

    GET_INFO_SUCCESS("상세 조회 성공", HttpStatus.OK),
    POST_NOT_INFO("상세 내용이 없습니다.", HttpStatus.NOT_FOUND),
    GET_POST_ALL_SUCCESS("레시피 목록 조회 성공", HttpStatus.OK),
    GET_POST_DETAIL_SUCCESS("레시피 상세 조회 성공", HttpStatus.OK),
    GET_MY_RECIPE_COUNT("나의 레시피 조회 성공", HttpStatus.OK),
    GET_PRESIGNED_URL_SUCCESS("Presigned Url 가져오기 성공", HttpStatus.OK),
    MY_RECIPE_NOT_FOUND("나의 레시피가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    SORT_TYPE_NOT_FOUND("정렬 타입을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WRITE_SUCCESS("레시피 작성 성공", HttpStatus.OK),
    UPDATE_SUCCESS("레시피 수정 성공", HttpStatus.OK),
    POST_NOT_UPDATED("변경된 내용이 없습니다.",  HttpStatus.BAD_REQUEST),
    RECIPE_CREATE_ERROR("레시피 작성에 실패하였습니다.", HttpStatus.BAD_REQUEST),
    DELETE_POST_SUCCESS("레시피 삭제 성공", HttpStatus.OK),
    DELETE_DETAIL_SUCCESS("상세 내용 삭제 성공", HttpStatus.OK),
    RECIPE_IS_EMPTY("레시피가 존재하지 않습니다.", HttpStatus.NO_CONTENT),
    POST_NOT_FOUND("해당 레시피를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}
