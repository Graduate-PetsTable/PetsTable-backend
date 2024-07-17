package com.example.petstable.domain.member.controller;

import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.dto.response.BookmarkMyList;
import com.example.petstable.domain.member.dto.response.OAuthMemberSignUpResponse;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.global.auth.ios.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.petstable.domain.bookmark.message.BookmarkMessage.SUCCESS_GET_MY_BOOKMARK;
import static com.example.petstable.domain.member.message.MemberMessage.*;

@Tag(name = "사용자 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "닉네임 설정", description = "최종 회원가입 느낌")
    @PostMapping
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<OAuthMemberSignUpResponse> signUp(@RequestBody @Valid OAuthMemberSignUpRequest request) {
        OAuthMemberSignUpResponse response = memberService.signUpByOAuthMember(request);

        return PetsTableApiResponse.createResponse(response, JOIN_SUCCESS);
    }

    @Operation(summary = "내 북마크 목록 조회")
    @GetMapping("/bookmark")
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<BookmarkMyList> getMyBookmark(@LoginUserId Long memberId) {

        BookmarkMyList response = memberService.findMyBookmarkList(memberId);

        return PetsTableApiResponse.createResponse(response, SUCCESS_GET_MY_BOOKMARK);
    }
}
