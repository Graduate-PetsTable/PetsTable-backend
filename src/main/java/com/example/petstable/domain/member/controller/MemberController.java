package com.example.petstable.domain.member.controller;

import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.dto.response.OAuthMemberSignUpResponse;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.petstable.global.exception.message.MemberMessage.*;

@Tag(name = "사용자 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "닉네임 설정", description = "최종 회원가입 느낌")
    @PostMapping
    public PetsTableApiResponse<OAuthMemberSignUpResponse> signUp(@RequestBody @Valid OAuthMemberSignUpRequest request) {
        OAuthMemberSignUpResponse response = memberService.signUpByOAuthMember(request);

        return PetsTableApiResponse.createResponse(response, JOIN_SUCCESS);
    }
}
