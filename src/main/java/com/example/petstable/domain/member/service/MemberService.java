package com.example.petstable.domain.member.service;

import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.dto.response.OAuthMemberSignUpResponse;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.petstable.global.exception.message.MemberMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public OAuthMemberSignUpResponse signUpByOAuthMember(OAuthMemberSignUpRequest request) {

        validateDuplicateMember(request);
        SocialType socialType = SocialType.from(request.getSocialType());
        MemberEntity findMember = memberRepository.findBySocialTypeAndSocialId(socialType, request.getSocialId())
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));
        findMember.registerOAuthMember(request.getEmail(), request.getNickname());

        return new OAuthMemberSignUpResponse(findMember.getId(), findMember.getNickName());
    }

    private void validateDuplicateMember(OAuthMemberSignUpRequest memberSignUpRequest) {
        memberRepository.findByEmail(memberSignUpRequest.getEmail())
                .ifPresent(member -> {
                    throw new PetsTableException(INVALID_EMAIL.getStatus(), INVALID_EMAIL.getMessage(), 409);
                });
        memberRepository.findByNickName(memberSignUpRequest.getNickname())
                .ifPresent(member -> {
                    throw new PetsTableException(INVALID_NICKNAME.getStatus(), INVALID_NICKNAME.getMessage(), 409);
                });
    }
}
