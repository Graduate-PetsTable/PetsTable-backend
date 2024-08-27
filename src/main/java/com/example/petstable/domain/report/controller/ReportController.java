package com.example.petstable.domain.report.controller;

import com.example.petstable.domain.report.dto.request.ReportPostRequest;
import com.example.petstable.domain.report.dto.response.ReportPostResponse;
import com.example.petstable.domain.report.service.ReportService;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.petstable.domain.report.message.ReportMessage.*;

@Tag(name = "신고 관련 컨트롤")
@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "게시글 신고")
    @PostMapping("/post/{postId}")
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<ReportPostResponse> reportPost(@LoginUserId Long memberId, @PathVariable("postId") Long postId, @RequestBody @Valid ReportPostRequest request) {

        ReportPostResponse response = reportService.reportPost(memberId, postId, request.getReportReason());

        return PetsTableApiResponse.createResponse(response, REPORT_POST_SUCCESS);
    }
}
