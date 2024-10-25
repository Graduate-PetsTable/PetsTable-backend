package com.example.petstable.domain.point.entity;

import com.example.petstable.global.exception.PetsTableException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

import static com.example.petstable.domain.member.message.MemberMessage.INVALID_SOCIAL;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    POINT_GAINED("획득"), POINT_USED("차감");

    private final String description;

    public static TransactionType from(String value) {
        return Arrays.stream(values())
                .filter(it -> Objects.equals(it.description, value))
                .findFirst()
                .orElseThrow(() -> new PetsTableException(INVALID_SOCIAL.getStatus(), INVALID_SOCIAL.getMessage(), 400));
    }
}