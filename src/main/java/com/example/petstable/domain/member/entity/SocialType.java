package com.example.petstable.domain.member.entity;

import com.example.petstable.global.exception.PetsTableException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

import static com.example.petstable.global.exception.message.MemberMessage.*;

@AllArgsConstructor @NoArgsConstructor
@Getter
public enum SocialType {

    GOOGLE("google"),
    APPLE("apple");

    private String value;

    public static SocialType from(String value) {
        return Arrays.stream(values())
                .filter(it -> Objects.equals(it.value, value))
                .findFirst()
                .orElseThrow(() -> new PetsTableException(INVALID_SOCIAL.getStatus(), INVALID_SOCIAL.getMessage(), 400));
    }
}
