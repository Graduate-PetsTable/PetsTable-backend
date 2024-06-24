package com.example.petstable.domain.board.controller;

import com.example.petstable.domain.board.dto.request.BoardRequest;
import com.example.petstable.domain.board.dto.request.BoardWithDetailsRequestAndTagRequest;
import com.example.petstable.domain.board.dto.request.DetailRequest;
import com.example.petstable.domain.board.dto.response.BoardPostResponse;
import com.example.petstable.domain.board.service.BoardService;
import com.example.petstable.domain.board.dto.request.TagRequest;
import com.example.petstable.global.auth.ios.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.IntStream;

import static com.example.petstable.domain.board.message.BoardMessage.*;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시글 작성 API")
    @PostMapping("/with-details")
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<BoardPostResponse> createPost(@LoginUserId Long memberId, @RequestBody BoardRequest boardRequest, @RequestPart("images") List<MultipartFile> images, @RequestBody List<String> description, List<String> tagType, List<String> tagName) {

        List<DetailRequest> detailRequests = IntStream.range(0, images.size())
                .mapToObj(i -> DetailRequest.builder()
                        .image_url(images.get(i))
                        .description(description.get(i))
                        .build())
                .toList();

        List<TagRequest> tagRequests = IntStream.range(0, tagName.size())
                .mapToObj(i -> TagRequest.builder()
                        .tagType(tagType.get(i))
                        .tagName(tagName.get(i))
                        .build())
                .toList();

        BoardWithDetailsRequestAndTagRequest boardWithDetailsRequestAndTagRequest = BoardWithDetailsRequestAndTagRequest.builder()
                .title(boardRequest.getTitle())
                .details(detailRequests)
                .tags(tagRequests)
                .build();

        BoardPostResponse response = boardService.writePost(memberId, boardWithDetailsRequestAndTagRequest);

        return PetsTableApiResponse.createResponse(response, WRITE_SUCCESS);
    }
}
