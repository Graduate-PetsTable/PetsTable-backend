package com.example.petstable.domain.bookmark.controller;

import com.example.petstable.domain.bookmark.dto.response.BookmarkRegisterResponse;
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
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "북마크 관련 API")
public interface BookmarkApi {
    @Operation(summary = "북마크 등록 및 취소 API", description = "북마크를 등록 혹은 취소하는 API 입니다.",
            parameters = @Parameter(name = "postId", description = "레시피 id", required = true),
            responses =  {
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = BookmarkRegisterResponse.class),
                    examples = @ExampleObject(value = """
                        {
                            "memberId": 1,
                            "postId": 10,
                            "status": true
                        }
                    """)
            ),
                    description = "북마크 등록에 성공하였습니다."
            ),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)

    })
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<BookmarkRegisterResponse> addBookmark(
            @LoginUserId Long memberId,
            @PathVariable("postId") Long postId
    );
}
