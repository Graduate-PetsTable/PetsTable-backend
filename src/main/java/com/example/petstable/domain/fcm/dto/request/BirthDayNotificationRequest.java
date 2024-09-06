package com.example.petstable.domain.fcm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BirthDayNotificationRequest {

    @Schema(description = "특정 회원 ID", example = "5")
    private Long memberId;

    @Schema(description = "반려동물 이름", example = "장군이")
    private String name;

    @Schema(description = "반려동물 나이", example = "6")
    private int age;
}
