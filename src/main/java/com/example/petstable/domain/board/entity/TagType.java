package com.example.petstable.domain.board.entity;

import com.example.petstable.global.exception.PetsTableException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

import static com.example.petstable.domain.board.message.TagMessage.*;


@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum TagType {

    SIZE("크기별"),
    AGE("나이별"),
    DRY("건조별"),
    FUNCTION("기능별"),
    READY_TIME("준비 시간");

    private String type;

    public static TagType from(String type) {
        return Arrays.stream(values())
                .filter(it -> Objects.equals(it.type, type))
                .findFirst()
                .orElseThrow(() -> new PetsTableException(INVALID_TAG.getStatus(), INVALID_TAG.getMessage(), 400));
    }
}
