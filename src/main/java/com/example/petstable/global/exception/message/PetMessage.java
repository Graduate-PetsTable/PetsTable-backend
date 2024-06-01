package com.example.petstable.global.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PetMessage implements ResponseMessage {

    CREATE_SUCCESS("반려동물이 성공적으로 등록되었습니다.", HttpStatus.OK),
    GET_ALL_SUCCESS("반려동물이 성공적으로 전체 조회되었습니다.", HttpStatus.OK),
    GET_SUCCESS("반려동물이 성공적으로 조회되었습니다..", HttpStatus.OK),
    PETS_NOT_FOUND("해당 회원의 반료동물이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    PET_NOT_FOUND("해당 반려동물이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}
