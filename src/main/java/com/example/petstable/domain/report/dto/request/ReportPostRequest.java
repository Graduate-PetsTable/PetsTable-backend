package com.example.petstable.domain.report.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportPostRequest {

    @NotBlank(message = "공백일 수 없습니다.")
    @Schema(description = "신고 사유", example = "모욕", allowableValues = {"스팸", "정보유출", "성적인 내용", "부적절한 컨텐츠", "모욕", "광고", "정치적 발언"})
    private String reportReason;
}
