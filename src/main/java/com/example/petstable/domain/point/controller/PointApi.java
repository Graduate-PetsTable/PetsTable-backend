package com.example.petstable.domain.point.controller;

import com.example.petstable.domain.point.dto.response.PointResponse;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "포인트 관련 API")
@SecurityRequirement(name = "JWT")
public interface PointApi {

    @Operation(summary = "포인트 조회 API", description = "나의 포인트를 조회합니다",
            responses =  {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = PointResponse.class),
                            examples = @ExampleObject(value = """
                            {
                              "point": 10
                            }
                            """)
                    ),
                            description = "포인트 조회에 성공하였습니다."),
                    @ApiResponse(responseCode = "404", description = "포인트 정보를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류가 발했습니다.", content = @Content)
            })
    PetsTableApiResponse<PointResponse> getMyPoint(@Parameter(hidden = true) @LoginUserId Long memberId);

    @Operation(summary = "포인트 조회 API", description = "나의 포인트를 조회합니다",
            responses =  {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = PointResponse.class),
                            examples = @ExampleObject(value = """
                            {
                              "point": 10
                              "transactionType": 획득
                              "description": 레시피 작성
                              "createTime": 2024-10-25 15:30:45
                            }
                            """)
                    ),
                            description = "포인트 조회에 성공하였습니다."),
                    @ApiResponse(responseCode = "404", description = "포인트 정보를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류가 발했습니다.", content = @Content)
            })
    PetsTableApiResponse<PointResponse> getMyPointHistory(@Parameter(hidden = true) @LoginUserId Long memberId);
}
