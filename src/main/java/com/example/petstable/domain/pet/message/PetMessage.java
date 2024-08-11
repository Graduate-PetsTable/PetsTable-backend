package com.example.petstable.domain.pet.message;

import com.example.petstable.global.exception.ResponseMessage;
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
    ALREADY_EXISTS_PET("이미 등록된 반려동물입니다.", HttpStatus.BAD_REQUEST),
    SUCCESS_REGISTER_PET_IMAGE("반려동물 사진 등록에 성공하였습니다.", HttpStatus.OK),
    PET_INFO_UPDATE_SUCCESS("반려동물 정보가 업데이트 되었습니다.", HttpStatus.OK),
    PET_NOT_FOUND("해당 반려동물이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}
