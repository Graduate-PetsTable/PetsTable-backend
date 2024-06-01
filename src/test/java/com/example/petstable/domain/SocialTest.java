package com.example.petstable.domain;

import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.global.exception.PetsTableException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SocialTest {

    @Test
    @DisplayName("SocialType 올바르게 반환")
    void from() {
        SocialType expected = SocialType.APPLE;

        SocialType actual = SocialType.from("apple");

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("SocialType 올바르지 않으면 예외 반환")
    void fromInvalid() {
        SocialType expected = SocialType.GOOGLE;

        Assertions.assertThatThrownBy(() -> SocialType.from(null))
                .isInstanceOf(PetsTableException.class);
    }
}
