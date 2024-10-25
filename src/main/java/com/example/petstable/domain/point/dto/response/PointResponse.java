package com.example.petstable.domain.point.dto.response;

import com.example.petstable.domain.point.entity.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointResponse {
    private int point;
    private TransactionType transactionType;
    private String description;
    private LocalDateTime createTime;
}
