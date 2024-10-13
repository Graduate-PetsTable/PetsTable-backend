package com.example.petstable.domain.point.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    POINT_GAINED("획득"), POINT_USED("차감");

    private final String description;
}