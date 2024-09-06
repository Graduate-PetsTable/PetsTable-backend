package com.example.petstable.domain.member.controller;

import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.dto.response.BookmarkMyList;
import com.example.petstable.domain.member.dto.response.MemberProfileImageResponse;
import com.example.petstable.domain.member.dto.response.OAuthMemberSignUpResponse;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.petstable.domain.bookmark.message.BookmarkMessage.SUCCESS_GET_MY_BOOKMARK;
import static com.example.petstable.domain.member.message.MemberMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController implements MemberApi{

    private final MemberService memberService;

    @PostMapping
    public PetsTableApiResponse<OAuthMemberSignUpResponse> signUp(@RequestBody @Valid OAuthMemberSignUpRequest request) {
        OAuthMemberSignUpResponse response = memberService.signUpByOAuthMember(request);

        return PetsTableApiResponse.createResponse(response, JOIN_SUCCESS);
    }

    @PatchMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PetsTableApiResponse<MemberProfileImageResponse> addProfileImage(@LoginUserId Long memberId, @RequestPart("multipartFile") MultipartFile multipartFile) {

        MemberProfileImageResponse response = memberService.registerProfileImage(memberId, multipartFile);

        return PetsTableApiResponse.createResponse(response, SUCCESS_REGISTER_PROFILE_IMAGE);
    }

    @DeleteMapping(value = "/profile/image")
    public PetsTableApiResponse<MemberProfileImageResponse> deleteProfileImage(@LoginUserId Long memberId) {

        MemberProfileImageResponse response = memberService.deleteProfileImage(memberId);

        return PetsTableApiResponse.createResponse(response, SUCCESS_DELETE_PROFILE_IMAGE);
    }

    @GetMapping("/bookmark")
    public PetsTableApiResponse<BookmarkMyList> getMyBookmark(@LoginUserId Long memberId) {

        BookmarkMyList response = memberService.findMyBookmarkList(memberId);

        return PetsTableApiResponse.createResponse(response, SUCCESS_GET_MY_BOOKMARK);
    }
}
