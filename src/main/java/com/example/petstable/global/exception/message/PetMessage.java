package com.example.petstable.global.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PetMessage {

    PET_EXISTS("이미 등록된 반려동물입니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;
}
