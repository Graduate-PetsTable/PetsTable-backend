package com.example.petstable.domain.point.dto.request;

import com.example.petstable.domain.point.entity.TransactionType;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class PointRequest {
    @Builder.Default
    @Min(0)
    private final int point = 10;
    @Builder.Default
    private final TransactionType type = TransactionType.POINT_GAINED;
    @Builder.Default
    private final String description = "레시피 작성";

    public static PointRequest of(int point, final TransactionType type, final String description) {
        return PointRequest.builder()
                .point(point)
                .type(type)
                .description(description)
                .build();
    }
}