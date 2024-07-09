package com.example.petstable.domain.board.service;

import com.example.petstable.domain.board.dto.request.*;
import com.example.petstable.domain.board.dto.response.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public BoardPostResponse writePost(Long memberId, BoardPostRequest request, List<MultipartFile> images) {

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        BoardWithDetailsRequestAndTagRequest boardRequest = BoardWithDetailsRequestAndTagRequest.builder()
                .title(request.getTitle())
                .details(IntStream.range(0, images.size())
                        .mapToObj(i -> DetailRequest.builder()
                                .image_url(images.get(i))
                                .description(request.getDescriptions().get(i).getDescription())
                                .build())
                        .toList())
                .tags(request.getTags())
                .build();

        BoardEntity post = createPostWithDetailsAndTags(boardRequest, member);

        boardRepository.save(post);

        return BoardPostResponse.builder()
                .title(post.getTitle())
                .details(post.getDetails().stream()
                        .distinct() // 중복된 객체 제거
                        .map(detail -> DetailPostResponse.builder()
                                .image_url(detail.getImage_url())
                                .description(detail.getDescription())
                                .build())
                        .collect(Collectors.toList()))
                .tags(post.getTags().stream()
                        .distinct() // 중복된 객체 제거
                        .map(tag -> TagResponse.builder()
                                .tagType(tag.getType())
                                .tagName(tag.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // 연관관계 설정
    private BoardEntity createPostWithDetailsAndTags(BoardWithDetailsRequestAndTagRequest request, MemberEntity member) {

        BoardEntity post = BoardEntity.builder()
                .title(request.getTitle())
                .build();

        member.addPost(post);
        post.setMember(member);

        List<DetailEntity> details = request.getDetails().stream()
                .map(detailRequest -> {
                    String imageUrl = awsS3Uploader.uploadImage(detailRequest.getImage_url());
                    String description = detailRequest.getDescription();
                    return DetailEntity.builder()
                            .image_url(imageUrl)
                            .description(description)
                            .post(post)
                            .build();
                })
                .toList();

        String thumbnail_url = details.get(details.size() - 1).getImage_url();

        post.setThumbnail_url(thumbnail_url);
        post.addDetails(details);
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

    public BoardDetailReadResponse findDetailByBoardId(Long boardId) {

        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new PetsTableException(POST_NOT_FOUND.getStatus(), POST_NOT_FOUND.getMessage(), 404));

        boardEntity.increaseViewCount();

        return BoardDetailReadResponse.from(boardEntity);
    }
}
