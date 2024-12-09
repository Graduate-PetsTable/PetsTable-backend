package com.example.petstable.domain.board.controller;

import com.example.petstable.domain.board.dto.request.*;
import com.example.petstable.domain.board.dto.response.*;
import com.example.petstable.domain.board.service.BoardService;
import com.example.petstable.domain.board.service.RecipeViewCntService;
import com.example.petstable.domain.detail.dto.request.DetailUpdateRequest;
import com.example.petstable.domain.detail.dto.response.DetailResponse;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.petstable.domain.board.message.BoardMessage.*;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class BoardController implements BoardApi {
    private final BoardService boardService;
    private final RecipeViewCntService recipeViewCntService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PetsTableApiResponse<BoardPostResponse> createPost(@LoginUserId Long memberId, @RequestPart(value = "request", required = false) BoardPostRequest request, @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail, @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        BoardPostResponse response = boardService.writePost(memberId, request, thumbnail, images);
        return PetsTableApiResponse.createResponse(response, WRITE_SUCCESS);
    }

    @PostMapping("/preSignedUrl")
    public PetsTableApiResponse<List<PreSignedUrlResponse>> getPreSignedUrl(@RequestBody List<PreSignedUrlRequest> preSignedUrlRequestList) {
        List<PreSignedUrlResponse> response = boardService.getPresignedUrl(preSignedUrlRequestList);
        return PetsTableApiResponse.createResponse(response, GET_PRESIGNED_URL_SUCCESS);
    }

    @PostMapping(value = "/withPreSignedUrl")
    public PetsTableApiResponse<BoardPostResponse> createPostV2(@LoginUserId Long memberId, @RequestBody(required = false) BoardPostRequestWithPresignedUrl request) {
        BoardPostResponse response = boardService.writePostV2(memberId, request);
        return PetsTableApiResponse.createResponse(response, WRITE_SUCCESS);
    }

    @GetMapping
    public PetsTableApiResponse<BoardReadAllResponse> readAllPost(Pageable pageable, @LoginUserId Long memberId) {
        BoardReadAllResponse response = boardService.getAllPost(pageable, memberId);
        return PetsTableApiResponse.createResponse(response, GET_POST_ALL_SUCCESS);
    }

    @GetMapping("/v2")
    public PetsTableApiResponse<BoardReadAllResponse> readAllPostV2(Pageable pageable, @LoginUserId Long memberId, @RequestParam("sortBy") String sortBy) {
        BoardReadAllResponse response = boardService.getAllPostV2(pageable, memberId, sortBy);
        return PetsTableApiResponse.createResponse(response, GET_POST_ALL_SUCCESS);
    }

    @GetMapping("/search")
    public PetsTableApiResponse<List<BoardReadResponse>> readPostsByFiltering(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "tagNames", required = false) List<String> tagNames,
            @RequestParam(value = "ingredientNames", required = false) List<String> ingredientNames,
            Pageable pageable, @LoginUserId Long memberId) {
        BoardFilteringRequest request = new BoardFilteringRequest(keyword, tagNames, ingredientNames);
        if (request.getKeyword() != null) {
            List<BoardReadResponse> response = boardService.findPostsByTitleAndContent(request, pageable, memberId);
            return PetsTableApiResponse.createResponse(response, GET_POST_ALL_SUCCESS);
        } else {
            List<BoardReadResponse> response = boardService.findPostsByTagAndIngredients(request, pageable, memberId);
            return PetsTableApiResponse.createResponse(response, GET_POST_ALL_SUCCESS);
        }
    }

    @GetMapping("/my")
    public PetsTableApiResponse<BoardResponse> getAllByMyRecipe(@LoginUserId Long memberId) {
        BoardResponse response = boardService.getMyRecipe(memberId);
        return PetsTableApiResponse.createResponse(response, GET_POST_DETAIL_SUCCESS);
    }

    @GetMapping("/{postId}")
    public PetsTableApiResponse<BoardDetailReadResponse> getPostDetail(@LoginUserId Long memberId, @PathVariable("postId") Long postId) {
        recipeViewCntService.updateViewCount(memberId, postId);
        BoardDetailReadResponse response = boardService.findDetailByPostId(memberId, postId);
        return PetsTableApiResponse.createResponse(response, GET_POST_DETAIL_SUCCESS);
    }

    @DeleteMapping(value = "/{postId}")
    public ResponseEntity<String> deletePost (@LoginUserId Long userId, @PathVariable("postId") Long postId) {
        boardService.deletePost(userId, postId);
        return ResponseEntity.ok(DELETE_POST_SUCCESS.getMessage());
    }

    @PatchMapping(value = "/{postId}/title")
    public ResponseEntity<String> updatePostTitle(@LoginUserId Long userId, @PathVariable("postId") Long postId, @RequestBody BoardUpdateTitleRequest request) {
        boardService.updatePostTitle(userId, postId, request);
        return ResponseEntity.ok(UPDATE_SUCCESS.getMessage());
    }

    @PostMapping(value = "/{postId}/tag/{tagId}")
    public ResponseEntity<String> updatePostTag(@LoginUserId Long userId, @PathVariable("postId") Long postId, @RequestBody List<BoardUpdateTagRequest> request) {
        boardService.updatePostTags(userId, postId, request);
        return ResponseEntity.ok(UPDATE_SUCCESS.getMessage());
    }

    @PatchMapping(value = "/{postId}/details/{detailId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updatePostDetail(@LoginUserId Long userId, @PathVariable("postId") Long postId, @PathVariable("detailId") Long detailId, @RequestPart(name = "request", required = false) DetailUpdateRequest request, @RequestPart(name = "image", required = false) MultipartFile image) {
        boardService.updatePostDetail(userId, postId, detailId, request, image);
        return ResponseEntity.ok(UPDATE_SUCCESS.getMessage());
    }

    @DeleteMapping(value = "/{postId}/details/{detailId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PetsTableApiResponse<DetailResponse> deletePostDetail (@LoginUserId Long userId, @PathVariable("postId") Long postId, @PathVariable("detailId") Long detailId) {
        DetailResponse response = boardService.deletePostDetail(userId, postId, detailId);
        return PetsTableApiResponse.createResponse(response, DELETE_DETAIL_SUCCESS);
    }
}
