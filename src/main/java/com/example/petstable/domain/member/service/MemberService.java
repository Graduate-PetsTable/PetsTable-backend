package com.example.petstable.domain.member.service;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.bookmark.repository.BookmarkRepository;
import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.dto.response.BookmarkMyList;
import com.example.petstable.domain.member.dto.response.MemberProfileImageResponse;
import com.example.petstable.domain.member.dto.response.OAuthMemberSignUpResponse;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.point.entity.PointEntity;
import com.example.petstable.domain.point.repository.PointRepository;
import com.example.petstable.global.exception.PetsTableException;
import com.example.petstable.global.support.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.petstable.domain.member.message.MemberMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PointRepository pointRepository;
    private final AwsS3Uploader awsS3Uploader;

    @Transactional
    public OAuthMemberSignUpResponse signUpByOAuthMember(OAuthMemberSignUpRequest request) {

//        validateDuplicateMember(request);
        SocialType socialType = SocialType.from(request.getSocialType());
        MemberEntity findMember = memberRepository.findBySocialTypeAndSocialId(socialType, request.getSocialId())
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));
        PointEntity pointEntity = PointEntity.createPoint(findMember);
        pointRepository.save(pointEntity);

        findMember.updateNickname(request.getNickname());

        return new OAuthMemberSignUpResponse(findMember.getId(), findMember.getNickName());
    }

    private void validateDuplicateMember(OAuthMemberSignUpRequest memberSignUpRequest) {
        memberRepository.findByNickName(memberSignUpRequest.getNickname())
                .ifPresent(member -> {
                    throw new PetsTableException(INVALID_NICKNAME.getStatus(), INVALID_NICKNAME.getMessage(), 409);
                });
    }

    public MemberEntity validateMember(Long memberId) {

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));
    }

    public BookmarkMyList findMyBookmarkList(Long memberId) {

        validateMember(memberId);
        List<BoardEntity> findMyBookmarkList = bookmarkRepository.findBookmarkedPostsByMemberId(memberId);

        return BookmarkMyList.createBookmarkMyRegisterResponse(findMyBookmarkList);
    }

    @Transactional
    public MemberProfileImageResponse registerProfileImage(Long memberId, MultipartFile multipartFile) {

        MemberEntity member = validateMember(memberId);
        String imageUrl = awsS3Uploader.uploadImage(multipartFile);

        member.updateProfileImage(imageUrl);

        return MemberProfileImageResponse.builder()
                .id(member.getId())
                .imageUrl(imageUrl)
                .build();
    }

    @Transactional
    public MemberProfileImageResponse deleteProfileImage(Long memberId) {

        MemberEntity member = validateMember(memberId);

        member.updateProfileImage(null);

        return MemberProfileImageResponse.builder()
                .id(member.getId())
                .imageUrl(null)
                .build();
    }
}
