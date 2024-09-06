package com.example.petstable.domain.report.controller;

import com.example.petstable.domain.report.dto.request.ReportPostRequest;
import com.example.petstable.domain.report.dto.response.ReportPostResponse;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "신고 관련 API")
public interface ReportApi {

    @Operation(summary = "게시글 신고 API ", description = "게시글 신고하는 API 입니다.",
            parameters =  {
                @Parameter(name = "postId", description = "레시피 id", required = true)
            },
            responses = {
                @ApiResponse(responseCode = "200", content = @Content(
                        schema = @Schema(implementation = ReportPostResponse.class),
                        examples = @ExampleObject(value = """
                        {
                            "postReportCount": 3
                        }
                        """)
                ),
                description = "해당 레시피 신고를 완료했습니다."
                ),
                @ApiResponse(responseCode = "400", description = "이미 신고한 게시글입니다.", content = @Content),
                @ApiResponse(responseCode = "400", description = "내 게시글을 신고할 수 없습니다.", content = @Content),
                @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
                @ApiResponse(responseCode = "404", description = "해당 레시피를 찾을 수 없습니다.", content = @Content),
                @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            }
    )
    @SecurityRequirement(name = "JWT")
    PetsTableApiResponse<ReportPostResponse> reportPost(
            @Parameter(hidden = true) @LoginUserId Long memberId,
            @PathVariable("postId") Long postId,
            @RequestBody(description = "신고 사유 처리 데이터") ReportPostRequest request
    );
}
