package com.example.petstable.domain.member.controller;

import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.dto.response.BookmarkMyList;
import com.example.petstable.domain.member.dto.response.MemberProfileImageResponse;
import com.example.petstable.domain.member.dto.response.OAuthMemberSignUpResponse;
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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자 관련 API")
@SecurityRequirement(name = "JWT")
public interface MemberApi {

    @Operation(summary = "닉네임 설정 API", description = "닉네임 및 소셜 정보를 등록하는 API 입니다.",
            responses = {
                @ApiResponse(responseCode = "200", content = @Content(
                        schema = @Schema(implementation = OAuthMemberSignUpResponse.class),
                        examples = @ExampleObject(value = """
                        {
                            "id": 1,
                            "nickName": "블루"
                        }
                        """)
                ),
                        description = "닉네임 설정에 성공하였습니다."),
                @ApiResponse(responseCode = "400", description = "잘못된 요청입니다. 요청 데이터를 확인하세요.", content = @Content()),
                @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다. 인증을 확인하세요.", content = @Content()),
                @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content())
            }
    )
    PetsTableApiResponse<OAuthMemberSignUpResponse> signUp(
            @RequestBody(description = "닉네임 등록 요청 데이터") @Valid OAuthMemberSignUpRequest request
    );

    @Operation(summary = "사용자 프로필 이미지 등록 및 수정 API", description = "사용자 프로필 이미지를 등록 및 수정하는 API 입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = MemberProfileImageResponse.class),
                            examples = @ExampleObject(value = """
                        {
                            "id": 1,
                            "imageUrl": "https://example.com/image.jpg"
                        }
                        """)
                    ),
                            description = "프로필 이미지 등록/수정에 성공하였습니다."),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다. 파일을 확인하세요.", content = @Content()),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다. 인증을 확인하세요.", content = @Content()),
                    @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content())
            }
    )
    PetsTableApiResponse<MemberProfileImageResponse> addProfileImage(
            @Parameter(hidden = true) @LoginUserId Long memberId,
            @RequestPart("multipartFile") MultipartFile multipartFile
    );

    @Operation(summary = "사용자 프로필 이미지 삭제", description = "사용자 프로필 이미지를 삭제하는 API 입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = MemberProfileImageResponse.class),
                            examples = @ExampleObject(value = """
                        {
                            "memberId": 1,
                            "imageUrl": null
                        }
                        """)
                    ),
                            description = "프로필 이미지 삭제에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다. 인증을 확인하세요.", content = @Content()),
                    @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content())
            }
    )
    PetsTableApiResponse<MemberProfileImageResponse> deleteProfileImage(
            @Parameter(hidden = true) @LoginUserId Long memberId
    );

    @Operation(summary = "내 북마크 목록 조회", description = "내 북마크 목록을 조회하는 API 입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = BookmarkMyList.class),
                            examples = @ExampleObject(value = """
                        {
                            "status": true,
                            "recipes": [
                                {
                                    "id": 1,
                                    "title": "Example Title",
                                    "imageUrl": "https://example.com/image.jpg",
                                    "tagName": ["모질개선", "다이어트"]
                                }
                            ]
                        }
                        """)
                    ), description = "북마크 목록 조회에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다. 인증을 확인하세요.", content = @Content()),
                    @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content())
            })
    PetsTableApiResponse<BookmarkMyList> getMyBookmark(
            @Parameter(hidden = true) @LoginUserId Long memberId
    );
}
