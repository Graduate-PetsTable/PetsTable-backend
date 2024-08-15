package com.example.petstable.domain.board.service;

import com.example.petstable.domain.board.dto.request.*;
import com.example.petstable.domain.board.dto.response.*;
import com.example.petstable.domain.board.entity.*;
import com.example.petstable.domain.board.repository.BoardRepository;
import com.example.petstable.domain.board.repository.DetailRepository;
import com.example.petstable.domain.board.repository.IngredientRepository;
import com.example.petstable.domain.board.repository.TagRepository;
import com.example.petstable.domain.bookmark.repository.BookmarkRepository;
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
    private final BookmarkRepository bookmarkRepository;
    private final AwsS3Uploader awsS3Uploader;
    private final TagRepository tagRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public BoardPostResponse writePost(Long memberId, BoardPostRequest request, List<MultipartFile> images) {

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        BoardWithDetailsAndTagsAndIngredients boardRequest = BoardWithDetailsAndTagsAndIngredients.builder()
                .title(request.getTitle())
                .details(IntStream.range(0, images.size())
                        .mapToObj(i -> DetailRequest.builder()
                                .image(images.get(i))
                                .description(request.getDescriptions().get(i).getDescription())
                                .build())
                        .toList())
                .tags(request.getTags())
                .ingredients(request.getIngredients())
                .build();

        BoardEntity post = createPostWithDetailsAndTagsAndIngredientRequest(boardRequest, member);

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
                .ingredients(post.getIngredients().stream()
                        .distinct()
                        .map(ingredient -> IngredientResponse.builder()
                                .name(ingredient.getName())
                                .weight(ingredient.getWeight())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // 연관관계 설정
    private BoardEntity createPostWithDetailsAndTagsAndIngredientRequest(BoardWithDetailsAndTagsAndIngredients request, MemberEntity member) {

        BoardEntity post = BoardEntity.builder()
                .title(request.getTitle())
                .build();

        member.addPost(post);
        post.setMember(member);

        List<DetailEntity> details = request.getDetails().stream()
                .map(detailRequest -> {
                    String imageUrl = awsS3Uploader.uploadImage(detailRequest.getImage());
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


        List<IngredientEntity> ingredients = request.getIngredients().stream()
                .map(ingredientRequest -> IngredientEntity.builder()
                        .name(ingredientRequest.getName())
                        .weight(ingredientRequest.getWeight())
                        .build())
                .toList();

        post.addIngredient(ingredients);
        ingredientRepository.saveAll(ingredients);

        return post;
    }

    public BoardReadAllResponse getAllPost(Pageable pageable, Long memberId) {
        Page<BoardEntity> postPage = boardRepository.findAll(pageable);

        List<BoardReadWithBookmarkResponse> postResponses = postPage.stream()
                .map(post -> {
                    boolean isBookmarked = bookmarkRepository.existsByMemberIdAndPostId(memberId, post.getId());
                    return new BoardReadWithBookmarkResponse(post, isBookmarked);
                })
                .collect(Collectors.toList());

        PageResponse pageResponse = new PageResponse(postPage);

        if (postResponses.isEmpty()) {
            throw new PetsTableException(RECIPE_IS_EMPTY.getStatus(), RECIPE_IS_EMPTY.getMessage(), 204);
        }

        return new BoardReadAllResponse(postResponses, pageResponse);
    }

    public BoardEntity validMemberAndPost(Long userId, Long boardId) {

        MemberEntity member = memberRepository.findById(userId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        return boardRepository.findById(boardId)
                .orElseThrow(() -> new PetsTableException(POST_NOT_FOUND.getStatus(), POST_NOT_FOUND.getMessage(), 404));
    }

    @Transactional
    public BoardDetailReadResponse findDetailByBoardId(Long memberId, Long boardId) {

        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new PetsTableException(POST_NOT_FOUND.getStatus(), POST_NOT_FOUND.getMessage(), 404));

        boardEntity.increaseViewCount();

        boolean isBookmarked = bookmarkRepository.existsByMemberIdAndPostId(memberId, boardId);

        return BoardDetailReadResponse.from(boardEntity, isBookmarked);
    }

    @Transactional
    public void updatePostTitle(Long userId, Long boardId, BoardUpdateTitleRequest request) {

        BoardEntity post = validMemberAndPost(userId, boardId);

        post.updateTitle(request.getTitle());

        boardRepository.save(post);
    }

    @Transactional
    public void updatePostTags(Long memberId, Long boardId, List<BoardUpdateTagRequest> request) {

        BoardEntity post = validMemberAndPost(memberId, boardId);

        post.clearTags();
        tagRepository.deleteByPostId(boardId);

        List<TagEntity> newTags = request.stream()
                .map(tagRequest -> TagEntity.builder()
                        .type(TagType.from(tagRequest.getTagType()))
                        .name(tagRequest.getTagName())
                        .post(post)
                        .build())
                .toList();

        post.addTags(newTags);
        tagRepository.saveAll(newTags);
    }

    @Transactional
    public void updatePostDetail(Long userId, Long boardId, Long detailId, DetailUpdateRequest request, MultipartFile image) {

        BoardEntity post = validMemberAndPost(userId, boardId);

        DetailEntity detail = detailRepository.findById(detailId)
                .orElseThrow(() -> new PetsTableException(POST_NOT_INFO.getStatus(), POST_NOT_INFO.getMessage(), 400));

        String newDescription = request.getDescription();

        if (image != null && newDescription != null) { // 사진, 설명 변경
            String newImageUrl = awsS3Uploader.uploadImage(image);
            detail.updateImageUrlAndDescription(newImageUrl, newDescription);

        } else if (image != null) { // 사진만 변경
            String newImageUrl = awsS3Uploader.uploadImage(image);
            detail.updateImageUrl(newImageUrl);

        } else { // 설명만 변경
            detail.updateDescription(newDescription);
        }

        detailRepository.save(detail);
    }

    @Transactional
    public DetailResponse deletePostDetail(Long userId, Long boardId, Long detailId) {

        BoardEntity post = validMemberAndPost(userId, boardId);

        DetailEntity detail = detailRepository.findById(detailId)
                .orElseThrow(() -> new PetsTableException(POST_NOT_INFO.getStatus(), POST_NOT_INFO.getMessage(), 400));

        DetailResponse response = DetailResponse.builder()
                .description(detail.getDescription())
                .image_url(detail.getImage_url())
                .build();

        post.getDetails().remove(detail);

        detailRepository.delete(detail);

        return response;
    }

    @Transactional
    public void deletePost(Long userId, Long boardId) {

        BoardEntity post = validMemberAndPost(userId, boardId);

        boardRepository.delete(post);
    }
}
