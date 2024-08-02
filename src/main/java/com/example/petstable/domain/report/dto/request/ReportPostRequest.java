package com.example.petstable.domain.report.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportPostRequest {

    @NotBlank(message = "공백일 수 없습니다.")
    private String reportReason;
}
