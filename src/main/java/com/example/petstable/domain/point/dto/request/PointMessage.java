package com.example.petstable.domain.point.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
public class PointMessage {
    private String memberId;
    private String point;
    private String type;
    private String description;

    public static PointMessage of(Long memberId, PointRequest request) {
        return PointMessage.builder()
                .memberId(memberId.toString())
                .point(String.valueOf(request.getPoint()))
                .description(request.getDescription())
                .type(request.getType().name()).build();
    }

    public Map<String, String> toMap() {
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("memberId", memberId);
        fieldMap.put("point", point);
        fieldMap.put("type", type);
        fieldMap.put("description", description);
        return fieldMap;
    }
}