package com.example.petstable.domain.tag.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor @NoArgsConstructor
public enum TagType {

    SIZE("크기별"),
    AGE("나이별"),
    DRY("건조별"),
    FUNCTION("기능별"),
    READY_TIME("준비 시간");

    private String type;
}
