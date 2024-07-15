package com.example.petstable.domain.board.controller;

import com.example.petstable.domain.board.dto.request.*;
import com.example.petstable.domain.board.dto.response.BoardDetailReadResponse;
import com.example.petstable.domain.board.dto.response.BoardPostResponse;
import com.example.petstable.domain.board.dto.response.BoardReadAllResponse;
import com.example.petstable.domain.board.dto.response.DetailResponse;
import com.example.petstable.domain.board.service.BoardService;
import com.example.petstable.global.auth.ios.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.petstable.domain.board.message.BoardMessage.*;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시글 작성 API", description = "이미지는 formdata로, 제목, 설명, 태그는 모두 Json으로 보내주셔야합니다.")
    @PostMapping(value = "/with-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<BoardPostResponse> createPost(@LoginUserId Long memberId, @RequestPart("request") BoardPostRequest request, @RequestPart("images") List<MultipartFile> images) {

        BoardPostResponse response = boardService.writePost(memberId, request, images);

        return PetsTableApiResponse.createResponse(response, WRITE_SUCCESS);
    }

    @Operation(summary = "게시글 목록 전체 조회 API")
    @PostMapping("/list")
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<BoardReadAllResponse> readAllPost(Pageable pageable) {

        BoardReadAllResponse response = boardService.getAllPost(pageable);

        return PetsTableApiResponse.createResponse(response, GET_POST_ALL_SUCCESS);
    }

    @Operation(summary = "게시글 상세 조회 API")
    @PostMapping("/{boardId}/detail")
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<BoardDetailReadResponse> getPostDetail(@PathVariable("boardId") Long id) {

        BoardDetailReadResponse response = boardService.findDetailByBoardId(id);

        return PetsTableApiResponse.createResponse(response, GET_POST_DETAIL_SUCCESS);
    }

    @Operation(summary = "게시글 제목 수정 API", description = "제목만 수정 가능")
    @PatchMapping(value = "/{boardId}/title")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<String> updatePostTitle(@LoginUserId Long userId, @PathVariable("boardId") Long boardId, @RequestBody BoardUpdateTitleRequest request) {

        boardService.updatePostTitle(userId, boardId, request);

        return ResponseEntity.ok(UPDATE_SUCCESS.getMessage());
    }

    @Operation(summary = "게시글 태그 수정 API", description = "태그만 수정 가능 ( 기존에 있던 태그들 다 지우고 태그만 새로 추가하는 기능 ")
    @PostMapping(value = "/{boardId}/tag/{tagId}")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<String> updatePostTag(@LoginUserId Long userId, @PathVariable("boardId") Long boardId, @RequestBody List<BoardUpdateTagRequest> request) {

        boardService.updatePostTags(userId, boardId, request);

        return ResponseEntity.ok(UPDATE_SUCCESS.getMessage());
    }

    @Operation(summary = "게시글 상세 내용 수정 API", description = "선택한 단계만 수정 가능 ( 사진, 설명 )")
    @PatchMapping(value = "/{boardId}/img/{detailId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<String> updatePostDetail(@LoginUserId Long userId, @PathVariable("boardId") Long boardId, @PathVariable("detailId") Long detailId, @RequestBody DetailRequest request) {

        boardService.updatePostDetail(userId, boardId, detailId, request);

        return ResponseEntity.ok(UPDATE_SUCCESS.getMessage());
    }

    @Operation(summary = "게시글 상세 내용 삭제 API")
    @DeleteMapping(value = "/{boardId}/img/{detailId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<DetailResponse> deletePostDetail (@LoginUserId Long userId, @PathVariable("boardId") Long boardId, @PathVariable("detailId") Long detailId) {

        DetailResponse response = boardService.deletePostDetail(userId, boardId, detailId);

        return PetsTableApiResponse.createResponse(response, DELETE_DETAIL_SUCCESS);
    }

    @Operation(summary = "게시글 삭제 API")
    @DeleteMapping(value = "/{boardId}")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<String> deletePostDetail (@LoginUserId Long userId, @PathVariable("boardId") Long boardId) {

        boardService.deletePost(userId, boardId);

        return ResponseEntity.ok(DELETE_POST_SUCCESS.getMessage());
    }

}
