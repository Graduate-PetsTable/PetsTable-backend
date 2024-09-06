package com.example.petstable.domain.board.controller;

import com.example.petstable.domain.board.dto.request.BoardPostRequest;
import com.example.petstable.domain.board.dto.request.BoardUpdateTagRequest;
import com.example.petstable.domain.board.dto.request.BoardUpdateTitleRequest;
import com.example.petstable.domain.board.dto.request.DetailUpdateRequest;
import com.example.petstable.domain.board.dto.response.BoardDetailReadResponse;
import com.example.petstable.domain.board.dto.response.BoardPostResponse;
import com.example.petstable.domain.board.dto.response.BoardReadAllResponse;
import com.example.petstable.domain.board.dto.response.DetailResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@Tag(name = "레시피 관련 API")
@SecurityRequirement(name = "JWT")
public interface BoardApi {
    @Operation(summary = "레시피 작성 API", description = "레시피를 작성하는 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = BoardPostRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                          "title": "말티즈를 위한 닭죽 만들기",
                                          "descriptions": [
                                            {
                                              "description": "1단계 : 물 500mL 를 넣고 끓인다."
                                            }
                                          ],
                                          "tags": [
                                            {
                                              "tagType": "기능별",
                                              "tagName": "모질개선"
                                            }
                                          ],
                                          "ingredients": [
                                            {
                                              "name": "당근",
                                              "weight": "30g"
                                            }
                                          ]
                                    }
                                    """)
                    ),
                    description = "게시글 작성에 성공하였습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청입니다.",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content
            )})
    PetsTableApiResponse<BoardPostResponse> createPost(
            @Parameter(hidden = true) @LoginUserId Long memberId,
            @RequestPart("request") @Parameter(description = "레시피 내용 요청 데이터") BoardPostRequest request,
            @RequestPart("images") @Parameter(description = "내용에 알맞는 이미지들") List<MultipartFile> images
    );

    @Operation(summary = "레시피 목록 전체 조회 API", description = "레시피 목록을 전체 조회하는 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = BoardReadAllResponse.class),
                    examples = @ExampleObject(value = """
                    {
                      "recipes": [
                        {
                          "id": 1,
                          "title": "말티즈를 위한 닭죽 만들기",
                          "imageUrl": "https://example.com/image1.jpg",
                          "bookmarkStatus": true,
                          "tagName": [
                            "모질개선",
                            "저알러지"
                          ]
                        },
                        {
                          "id": 2,
                          "title": "포메라니안을 위한 건강 수프",
                          "imageUrl": "https://example.com/image2.jpg",
                          "bookmarkStatus": false,
                          "tagName": [
                            "체중조절",
                            "영양강화"
                          ]
                        }
                      ],
                      "pageResponse": {
                        "totalPages": 10,
                        "currentPage": 1,
                        "totalElements": 100,
                        "isPageLast": false,
                        "size": 10,
                        "numberOfElements": 10,
                        "isPageFirst": true,
                        "isEmpty": false
                      }
                    }
                    """)
            ),
                    description = "레시피 목록 조회에 성공하였습니다."
            ),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    PetsTableApiResponse<BoardReadAllResponse> readAllPost(
            Pageable pageable,
            @Parameter(hidden = true) @LoginUserId Long memberId
    );

    @Operation(summary = "레시피 상세 조회 API", description = "특정 레시피의 상세 정보를 조회하는 API입니다.",
            parameters = @Parameter(name = "boardId", description = "레시피 id", required = true),
            responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = BoardDetailReadResponse.class),
                    examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "title": "말티즈를 위한 닭죽 만들기",
                      "viewCount": 1041,
                      "bookmarkStatus": true,
                      "details": [
                        {
                          "image_url": "https://example.com/detail-image1.jpg",
                          "description": "1단계: 물 500mL를 끓인다."
                        },
                        {
                          "image_url": "https://example.com/detail-image2.jpg",
                          "description": "2단계: 닭고기를 넣고 20분간 끓인다."
                        }
                      ],
                      "tags": [
                        {
                          "tagType": "기능별",
                          "tagName": "모질개선"
                        },
                        {
                          "tagType": "기능별",
                          "tagName": "저알러지"
                        }
                      ],
                      "ingredients": [
                        {
                          "name": "닭고기",
                          "weight": "200g"
                        },
                        {
                          "name": "당근",
                          "weight": "50g"
                        }
                      ]
                    }
                    """)
            ),
                    description = "레시피 상세 조회에 성공하였습니다."
            ),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 레시피를 찾을 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    PetsTableApiResponse<BoardDetailReadResponse> getPostDetail(
            @Parameter(hidden = true) @LoginUserId Long memberId,
            @PathVariable("boardId") Long boardId
    );

    @Operation(summary = "레시피 삭제 API", description = "해당 레시피를 삭제하는 API 입니다.",
            parameters = @Parameter(name = "boardId", description = "레시피 id", required = true),
            responses = {
            @ApiResponse(responseCode = "200", description = "레시피 삭제에 성공하였습니다.", content = @Content(
                    examples = @ExampleObject(value = """
                    {
                        "message": "레시피 삭제에 성공하였습니다.",
                        "status": 200
                    }
                    """)
            )),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> deletePostDetail(
            @Parameter(hidden = true) @LoginUserId Long userId,
            @PathVariable("boardId") Long boardId
    );

    @Operation(summary = "레시피 제목 수정 API", description = "특정 레시피의 제목을 수정하는 API 입니다.",
            parameters = {
                    @Parameter(name = "boardId", description = "레시피 id", required = true)
            },
            responses = {
            @ApiResponse(responseCode = "200", description = "레시피 제목 수정에 성공하였습니다.", content = @Content(
                    schema = @Schema(implementation = PetsTableApiResponse.class),
                    examples = @ExampleObject(value = """
                    {
                        "message": "레시피 제목 수정에 성공하였습니다.",
                        "status": 200
                    }
                    """)
            )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> updatePostTitle(
            @Parameter(hidden = true) @LoginUserId Long userId,
            @PathVariable("boardId") Long boardId,
            @RequestBody(description = "제목 수정 요청 데이터", required = true) BoardUpdateTitleRequest request
    );

    @Operation(summary = "레시피 태그 수정 API", description = "특정 레시피의 태그를 수정하는 API입니다.",
            parameters = {
                    @Parameter(name = "boardId", description = "레시피 id", required = true),
            },
            responses = {
            @ApiResponse(responseCode = "200", description = "레시피 태그 수정에 성공하였습니다.", content = @Content(
                    schema = @Schema(implementation = PetsTableApiResponse.class),
                    examples = @ExampleObject(value = """
                    {
                        "message": "레시피 태그 수정에 성공하였습니다.",
                        "status": 200
                    }
                    """)
            )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 레시피의 태그를 찾을 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> updatePostTag(
            @Parameter(hidden = true) @LoginUserId Long userId,
            @PathVariable("boardId") Long boardId,
            @RequestBody(description = "태그 수정 요청 데이터", required = true) List<BoardUpdateTagRequest> request
    );

    @Operation(summary = "레시피 상세 내용 수정 API", description = "특정 레시피의 상세 내용을 수정하는 API 입니다.",
            parameters = {
                    @Parameter(name = "boardId", description = "레시피 id", required = true),
                    @Parameter(name = "detailId", description = "상세 내용 id", required = true)
            },
            responses = {
            @ApiResponse(responseCode = "200", description = "레시피 상세 내용 수정에 성공하였습니다.", content = @Content(
                    schema = @Schema(implementation = PetsTableApiResponse.class),
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                    {
                        "message": "레시피 상세 내용 수정에 성공하였습니다.",
                        "status": 200
                    }
                    """)
            )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 레시피의 상세 내용을 찾을 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> updatePostDetail(
            @Parameter(hidden = true) @LoginUserId Long userId,
            @PathVariable("boardId") Long boardId,
            @PathVariable("detailId") Long detailId,
            @RequestPart(name = "request", required = false) @Parameter(description = "변경할 내용") DetailUpdateRequest request,
            @RequestPart(name = "image", required = false) @Parameter(description = "내용에 알맞는 이미지") MultipartFile image
    );

    @Operation(summary = "레시피 상세 내용 삭제 API", description = "특정 레시피의 상세 내용을 삭제하는 API 입니다.",
            parameters = {
                    @Parameter(name = "boardId", description = "레시피 id", required = true),
                    @Parameter(name = "detailId", description = "상세 내용 id", required = true)
            },
            responses = {
            @ApiResponse(responseCode = "200", description = "레시피 상세 내용 삭제에 성공하였습니다.", content = @Content(
                    schema = @Schema(implementation = DetailResponse.class),
                    examples = @ExampleObject(value = """
                    {
                        "image_url": "https://example.com/detail-image1.jpg",
                        "description": "1단계: 물 500mL를 끓인다."
                    }
                    """)
            )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 레시피의 상세 내용을 찾을 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    PetsTableApiResponse<DetailResponse> deletePostDetail(
            @Parameter(hidden = true) @LoginUserId Long userId,
            @PathVariable("boardId") Long boardId,
            @PathVariable("detailId") Long detailId
    );
}
