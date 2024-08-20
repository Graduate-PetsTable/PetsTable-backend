package com.example.petstable.global.auth.apple;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplePublicKeys {

    private List<ApplePublicKey> keys;

    public ApplePublicKey getMatchesKey(String alg, String kid) {
        return this.keys.stream()
                .filter(k -> k.getAlg().equals(alg) && k.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Apple JWT 값의 alg, kid 정보가 올바르지 않습니다."));
    }
}
