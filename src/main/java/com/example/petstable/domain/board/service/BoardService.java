package com.example.petstable.domain.board.service;

import com.example.petstable.domain.board.dto.request.BoardWithDetailsRequestAndTagRequest;
import com.example.petstable.domain.board.dto.response.BoardPostResponse;
import com.example.petstable.domain.board.dto.response.BoardReadAllResponse;
import com.example.petstable.domain.board.dto.response.BoardReadResponse;
import com.example.petstable.domain.board.dto.response.PageResponse;
import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.entity.DetailEntity;
import com.example.petstable.domain.board.entity.TagEntity;
import com.example.petstable.domain.board.entity.TagType;
import com.example.petstable.domain.board.repository.BoardRepository;
import com.example.petstable.domain.board.repository.DetailRepository;
import com.example.petstable.domain.board.repository.TagRepository;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.global.exception.PetsTableException;
import com.example.petstable.global.support.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.petstable.domain.board.message.BoardMessage.*;
import static com.example.petstable.domain.member.message.MemberMessage.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final DetailRepository detailRepository;
    private final AwsS3Uploader awsS3Uploader;
    private final TagRepository tagRepository;

    @Transactional
    public BoardPostResponse writePost(Long memberId, BoardWithDetailsRequestAndTagRequest request) {

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        BoardEntity post = createPostWithDetailsAndTags(request, member);

        boardRepository.save(post);

        return BoardPostResponse.builder()
                .title(post.getTitle())
                .details(request.getDetails())
                .tags(request.getTags())
                .build();
    }

    // 연관관계 설정
    private BoardEntity createPostWithDetailsAndTags(BoardWithDetailsRequestAndTagRequest request, MemberEntity member) {

        BoardEntity post = BoardEntity.builder()
                .title(request.getTitle())
                .thumbnail_url(request.getThumbnail_url())
                .build();

        member.addPost(post);
        post.setMember(member);

        List<DetailEntity> details = request.getDetails().stream()
                .map(detailRequest -> {
                    String imageUrl = awsS3Uploader.uploadImage(detailRequest.getImage_url());
                    return DetailEntity.builder()
                            .image_url(imageUrl)
                            .description(detailRequest.getDescription())
                            .post(post)
                            .build();
                })
                .toList();

        post.addDescriptions(details);
        detailRepository.saveAll(details);

        List<TagEntity> tags = request.getTags().stream()
                .map(tagRequest -> TagEntity.builder()
                        .type(TagType.from(tagRequest.getTagType()))
                        .name(tagRequest.getTagName())
                        .post(post)
                        .build())
                .toList();

        post.addTags(tags);
        tagRepository.saveAll(tags);

        return post;
    }

    public BoardReadAllResponse getAllPost(Pageable pageable) {
        Page<BoardReadResponse> recipeTitlePage = boardRepository.findAll(pageable).map(BoardEntity::toBoardTitleAndTags);

        PageResponse pageResponse = new PageResponse(recipeTitlePage);
        if (recipeTitlePage.isEmpty()) {
            throw new PetsTableException(RECIPE_IS_EMPTY.getStatus(), RECIPE_IS_EMPTY.getMessage(), 204);
        }
        return new BoardReadAllResponse(recipeTitlePage.toList(), pageResponse);
    }
}
