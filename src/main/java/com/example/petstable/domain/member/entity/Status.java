package com.example.petstable.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
@Getter
public enum Status {

    ACTIVE("active"),
    INACTIVE("inactive"),
    DELETED("deleted");

    private String value;
}
