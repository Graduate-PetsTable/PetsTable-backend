package com.example.petstable.domain.board.controller;

import com.example.petstable.domain.board.dto.request.*;
import com.example.petstable.domain.board.dto.response.BoardDetailReadResponse;
import com.example.petstable.domain.board.dto.response.BoardPostResponse;
import com.example.petstable.domain.board.dto.response.BoardReadAllResponse;
import com.example.petstable.domain.board.service.BoardService;
import com.example.petstable.global.auth.ios.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
    @PostMapping("/{boardId}")
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<BoardDetailReadResponse> getPostDetail(@PathVariable("boardId") Long id) {

        BoardDetailReadResponse response = boardService.findDetailByBoardId(id);

        return PetsTableApiResponse.createResponse(response, GET_POST_DETAIL_SUCCESS);
    }
}
