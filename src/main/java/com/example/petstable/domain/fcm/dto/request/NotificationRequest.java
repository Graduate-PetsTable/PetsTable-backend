package com.example.petstable.domain.fcm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class NotificationRequest {

    @Schema(description = "특정 회원 ID", example = "5")
    private Long memberId;

    @Schema(description = "메시지 제목", example = "게시글 알림")
    private String title;

    @Schema(description = "메시지 내용", example = "홍길동님이 새로운 게시글을 올렸어요")
    private String body;
}
