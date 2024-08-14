package com.example.petstable.domain.fcm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class NotificationRequest {

    @Schema(description = "메시지 제목")
    private String title;

    @Schema(description = "메시지 내용")
    private String body;
}
