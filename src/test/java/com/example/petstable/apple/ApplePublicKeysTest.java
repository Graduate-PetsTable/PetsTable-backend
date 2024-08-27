package com.example.petstable.apple;

import com.example.petstable.global.auth.apple.ApplePublicKey;
import com.example.petstable.global.auth.apple.ApplePublicKeys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApplePublicKeysTest {

    @Test
    @DisplayName("alg, kid 값을 받아서 해당 public key 를 반환")
    void getMatchesKey() {
        ApplePublicKey expected = new ApplePublicKey("kty", "kid", "use", "alg", "n", "e");

        ApplePublicKeys applePublicKeys = new ApplePublicKeys(List.of(expected));

        assertThat(applePublicKeys.getMatchesKey("alg", "kid")).isEqualTo(expected);
    }

    @Test
    @DisplayName("alg, kid 값에 잘못된 값이 들어오면 예외를 반환")
    void getMatchesInvalidKey() {
        ApplePublicKey expected = new ApplePublicKey("kty", "kid", "use", "alg", "n", "e");

        ApplePublicKeys applePublicKeys = new ApplePublicKeys(List.of(expected));

        assertThatThrownBy(() -> applePublicKeys.getMatchesKey("aaa", "aaa"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
