package com.example.petstable.domain.board.service;

import com.example.petstable.domain.board.dto.request.*;
import com.example.petstable.domain.board.dto.response.*;
import com.example.petstable.domain.board.entity.*;
import com.example.petstable.domain.board.repository.*;
import com.example.petstable.domain.bookmark.repository.BookmarkRepository;
import com.example.petstable.domain.detail.dto.request.DetailRequest;
import com.example.petstable.domain.detail.dto.request.DetailUpdateRequest;
import com.example.petstable.domain.detail.dto.response.DetailResponse;
import com.example.petstable.domain.detail.entity.DetailEntity;
import com.example.petstable.domain.detail.repository.DetailRepository;
import com.example.petstable.domain.ingredient.dto.request.IngredientRequest;
import com.example.petstable.domain.ingredient.entity.IngredientEntity;
import com.example.petstable.domain.ingredient.repository.IngredientRepository;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.point.dto.request.PointRequest;
import com.example.petstable.domain.point.entity.TransactionType;
import com.example.petstable.domain.tag.dto.request.TagRequest;
import com.example.petstable.domain.tag.entity.TagEntity;
import com.example.petstable.domain.tag.entity.TagType;
import com.example.petstable.domain.tag.repository.TagRepository;
import com.example.petstable.global.config.AmazonConfig;
import com.example.petstable.global.exception.PetsTableException;
import com.example.petstable.global.support.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
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
    private final AmazonConfig amazonConfig;
    private final AsyncService asyncService;
    private final BoardRollbackService boardRollbackService;
    private final RecipeViewCntService recipeViewCntService;

    @Transactional
    public BoardPostResponse writePost(Long memberId, BoardPostRequest request, MultipartFile thumbnail, List<MultipartFile> images) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));
        BoardWithDetailsAndTagsAndIngredients boardRequest = createBoardRequest(request, thumbnail, images);
        BoardEntity post = createPostWithDetailsAndTagsAndIngredientRequest(boardRequest, member);
        boardRepository.save(post);
        return BoardPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .build();
    }

    public List<PreSignedUrlResponse> getPresignedUrl(List<PreSignedUrlRequest> request) {
        return request.stream()
                .map(preSignedUrlRequest -> awsS3Uploader.getPreSignedUrl(preSignedUrlRequest.getPrefix(), preSignedUrlRequest.getFileName()))
                .toList();
    }

    @Transactional
    @CacheEvict(value = "recipe", allEntries = true)
    public BoardPostResponse writePostV2(Long memberId, BoardPostRequestWithPresignedUrl request) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));
        BoardEntity post = BoardEntity.builder()
                .title(request.getTitle())
                .thumbnail_url(Optional.ofNullable(request.getThumbnailUrl()).orElse(null))
                .member(member)
                .build();
        BoardEntity newRecipe = boardRepository.save(post);
        RecipeWithDetailsAndTagsDto recipeWithDetailsAndTagsDto = asyncService.runAsyncTasks(RecipeCreateEvent.of(newRecipe, request.getDetails(), request.getTags(), request.getIngredients()));
        detailRepository.saveAll(recipeWithDetailsAndTagsDto.details());
        tagRepository.saveAll(recipeWithDetailsAndTagsDto.tags());
        ingredientRepository.saveAll(recipeWithDetailsAndTagsDto.ingredients());
        boardRepository.save(post);
        RecordId recordId = asyncService.publishEventMemberPoint(memberId, PointRequest.of(10, TransactionType.POINT_GAINED, "레시피 작성"));
        boardRollbackService.rollbackReipce(recordId);
        return BoardPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .build();
    }

    private BoardWithDetailsAndTagsAndIngredients createBoardRequest(BoardPostRequest request, MultipartFile thumbnail, List<MultipartFile> images) {
        List<DetailRequest> details = Optional.ofNullable(request.getDescriptions())
                .map(descriptions -> {
                    int size = Optional.ofNullable(images).map(List::size).orElse(0);
                    int maxSize = Math.max(size, descriptions.size());
                    return IntStream.range(0, maxSize)
                            .mapToObj(i -> {
                                String description = (i < descriptions.size()) ? descriptions.get(i).getDescription() : null;
                                MultipartFile image = (i < size) ? images.get(i) : null;
                                if (image == null && description != null) { // 이미지가 없고 설명이 있을 경우 설명만 반환
                                    return DetailRequest.builder()
                                            .image(null) // 이미지 필드는 null
                                            .description(description)
                                            .build();
                                } else if (image != null && description == null) { // 설명이 없고 이미지가 있을 경우 이미지만 반환
                                    return DetailRequest.builder()
                                            .image(image)
                                            .description(null) // 설명 필드는 null
                                            .build();
                                } else if (image != null && description != null) { // 둘 다 있는 경우
                                    return DetailRequest.builder()
                                            .image(image)
                                            .description(description)
                                            .build();
                                }
                                return null; // 둘 다 없는 경우는 null 반환
                            })
                            .filter(Objects::nonNull) // null이 아닌 항목만 필터링
                            .toList();
                }).orElse(Collections.emptyList());
        List<TagRequest> tags = Optional.ofNullable(request.getTags()).orElse(Collections.emptyList());
        List<IngredientRequest> ingredients = Optional.ofNullable(request.getIngredients()).orElse(Collections.emptyList());
        return BoardWithDetailsAndTagsAndIngredients.builder()
                .title(request.getTitle())
                .thumbnail(Optional.ofNullable(thumbnail).orElse(null))
                .details(details)
                .tags(tags)
                .ingredients(ingredients)
                .build();
    }

    private BoardEntity createPostWithDetailsAndTagsAndIngredientRequest(BoardWithDetailsAndTagsAndIngredients request, MemberEntity member) {
        String thumbnailUrl = Optional.ofNullable(request.getThumbnail())
                .map(thumbnail -> awsS3Uploader.uploadImage("recipe", thumbnail))
                .orElse(null);
        BoardEntity post = BoardEntity.builder()
                .title(request.getTitle())
                .thumbnail_url(thumbnailUrl)
                .build();
        member.addPost(post);
        post.setMember(member);
        Optional.ofNullable(request.getDetails())
                .ifPresent(details -> {
                    List<DetailEntity> detailEntities = details.stream()
                            .map(detailRequest -> {
                                String imageUrl = null;
                                if (detailRequest.getImage() != null) {
                                    imageUrl = awsS3Uploader.uploadImage("recipe", detailRequest.getImage());
                                }
                                return DetailEntity.builder()
                                        .image_url(imageUrl) // imageUrl이 null일 수 있음
                                        .description(detailRequest.getDescription())
                                        .post(post)
                                        .build();
                            })
                            .toList();
                    post.addDetails(detailEntities);
                    detailRepository.saveAll(detailEntities);
                });
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
        List<BoardReadResponse> postResponses = postPage.stream()
                .map(post -> {
                    boolean isBookmarked = bookmarkRepository.existsByMemberIdAndPostId(memberId, post.getId());
                    return new BoardReadResponse(post, isBookmarked);
                })
                .collect(Collectors.toList());
        PageResponse pageResponse = new PageResponse(postPage);
        if (postResponses.isEmpty()) {
            throw new PetsTableException(RECIPE_IS_EMPTY.getStatus(), RECIPE_IS_EMPTY.getMessage(), 204);
        }
        return new BoardReadAllResponse(postResponses, pageResponse);
    }

    private Page<BoardEntity> getRecipeSortedByLikes(Pageable pageable) {
        return boardRepository.findTopRecipeByViews(pageable);
    }

    private Page<BoardEntity> getCoursesSortedByLatest(Pageable pageable) {
        return boardRepository.findTopRecipeByCreatedTime(pageable);
    }

    @Cacheable(value = "recipe", key = "#sortBy + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public BoardReadAllResponse getAllPostV2(Pageable pageable, Long memberId, String sortBy) {
        Page<BoardEntity> postPage;
        if (sortBy.equalsIgnoreCase("POPULAR")) {
            postPage = getRecipeSortedByLikes(pageable);
        } else if (sortBy.equalsIgnoreCase("LATEST")) {
            postPage = getCoursesSortedByLatest(pageable);
        } else {
            throw new PetsTableException(SORT_TYPE_NOT_FOUND.getStatus(), SORT_TYPE_NOT_FOUND.getMessage(), 404);
        }
        List<BoardReadResponse> postResponses = postPage.stream()
                .map(post -> {
                    boolean isBookmarked = bookmarkRepository.existsByMemberIdAndPostId(memberId, post.getId());
                    // 만약 기존에 저장된 이미지의 url이 s3 도메인이라면 cloudfront 도메인으로 변경
                    String thumbnailUrl = post.getThumbnail_url();
                    post.setThumbnail_url(convertThumbnailUrlToCloudFront(thumbnailUrl));
                    return new BoardReadResponse(post, isBookmarked);
                })
                .collect(Collectors.toList());
        PageResponse pageResponse = new PageResponse(postPage);
        if (postResponses.isEmpty()) {
            throw new PetsTableException(RECIPE_IS_EMPTY.getStatus(), RECIPE_IS_EMPTY.getMessage(), 204);
        }
        return new BoardReadAllResponse(postResponses, pageResponse);
    }

    public List<BoardReadResponse> findPostsByTitleAndContent(BoardFilteringRequest request, Pageable pageable, Long memberId) {
        return boardRepository.findRecipesByQueryDslWithTitleAndContent(request, memberId, pageable);
    }

    public List<BoardReadResponse> findPostsByTagAndIngredients(BoardFilteringRequest request, Pageable pageable, Long memberId) {
        return boardRepository.findRecipesByQueryDslWithTagAndIngredients(request, memberId, pageable);
    }

    public BoardEntity validMemberAndPost(Long memberId, Long postId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));
        return boardRepository.findById(postId)
                .orElseThrow(() -> new PetsTableException(POST_NOT_FOUND.getStatus(), POST_NOT_FOUND.getMessage(), 404));
    }

    @Transactional
    public BoardDetailReadResponse findDetailByPostId(Long memberId, Long postId) {
        BoardEntity boardEntity = boardRepository.findById(postId)
                .orElseThrow(() -> new PetsTableException(POST_NOT_FOUND.getStatus(), POST_NOT_FOUND.getMessage(), 404));
        boolean isBookmarked = bookmarkRepository.existsByMemberIdAndPostId(memberId, postId);
        int viewCnt = recipeViewCntService.getViewCnt(postId);
        return BoardDetailReadResponse.from(boardEntity, isBookmarked, viewCnt, amazonConfig);
    }

    @Transactional
    @CacheEvict(value = "recipe", allEntries = true)
    public void updatePostTitle(Long memberId, Long postId, BoardUpdateTitleRequest request) {
        BoardEntity post = validMemberAndPost(memberId, postId);
        post.updateTitle(request.getTitle());
        boardRepository.save(post);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "recipe", allEntries = true),
            @CacheEvict(value = "myRecipe", key = "#memberId")
    })
    public void updatePostTags(Long memberId, Long postId, List<BoardUpdateTagRequest> request) {
        BoardEntity post = validMemberAndPost(memberId, postId);
        post.clearTags();
        tagRepository.deleteByPostId(postId);
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
    public void updatePostDetail(Long memberId, Long postId, Long detailId, DetailUpdateRequest request, MultipartFile image) {
        validMemberAndPost(memberId, postId);
        DetailEntity detail = detailRepository.findById(detailId)
                .orElseThrow(() -> new PetsTableException(POST_NOT_INFO.getStatus(), POST_NOT_INFO.getMessage(), 400));
        String newDescription = request.getDescription();
        if (image != null && newDescription != null) { // 사진, 설명 변경
            String newImageUrl = awsS3Uploader.uploadImage("recipe", image);
            detail.updateImageUrlAndDescription(newImageUrl, newDescription);
        } else if (image != null) { // 사진만 변경
            String newImageUrl = awsS3Uploader.uploadImage("recipe", image);
            detail.updateImageUrl(newImageUrl);
        } else { // 설명만 변경
            detail.updateDescription(newDescription);
        }
        detailRepository.save(detail);
    }

    @Transactional
    @CacheEvict(value = {"recipe", "myRecipe"}, allEntries = true)
    public DetailResponse deletePostDetail(Long memberId, Long postId, Long detailId) {
        BoardEntity post = validMemberAndPost(memberId, postId);
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
    @Caching(evict = {
            @CacheEvict(value = "recipe"),
            @CacheEvict(value = "myRecipe", key = "#memberId")
    })
    public void deletePost(Long memberId, Long postId) {
        BoardEntity post = validMemberAndPost(memberId, postId);
        boardRepository.delete(post);
    }

    @Cacheable(value = "myRecipe", key = "#memberId")
    public BoardResponse getMyRecipe(Long memberId) {
        List<BoardEntity> myRecipe = boardRepository.findAllByMemberId(memberId);
        if (myRecipe == null || myRecipe.isEmpty()) {
            throw new PetsTableException(MY_RECIPE_NOT_FOUND.getStatus(), MY_RECIPE_NOT_FOUND.getMessage(), 404);
        }
        List<BoardReadResponse> recipes = myRecipe
                .stream()
                .map(recipe -> {
                    String cloudfrontUrl = convertThumbnailUrlToCloudFront(recipe.getThumbnail_url());
                    recipe.setThumbnail_url(cloudfrontUrl);
                    return new BoardReadResponse(recipe);
                })
                .toList();
        return BoardResponse.builder()
                .count(recipes.size())
                .recipes(recipes)
                .build();
    }

    private String convertThumbnailUrlToCloudFront(String image) {
        if (image.startsWith(amazonConfig.getS3Uri())) {
            return image.replace(amazonConfig.getS3Uri(), amazonConfig.getCloudfrontUri());
        }
        return image;
    }
}