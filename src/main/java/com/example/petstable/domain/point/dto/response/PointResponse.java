package com.example.petstable.domain.point.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointResponse {
    private int point;
    private String transactionType;
    private String description;
    private LocalDateTime createTime;
}
