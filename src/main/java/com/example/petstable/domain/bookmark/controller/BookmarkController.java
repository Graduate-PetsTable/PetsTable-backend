package com.example.petstable.domain.bookmark.controller;

import com.example.petstable.domain.bookmark.dto.response.BookmarkRegisterResponse;
import com.example.petstable.domain.bookmark.service.BookmarkService;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.petstable.domain.bookmark.message.BookmarkMessage.*;

@Tag(name = "북마크 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "북마크 등록")
    @PostMapping("/{boardId}/bookmark")
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<BookmarkRegisterResponse> addBookmark(@LoginUserId Long memberId, @PathVariable("boardId") Long boardId) {

        BookmarkRegisterResponse response = bookmarkService.registerBookmark(memberId, boardId);

        if (response.isStatus()) {
            return PetsTableApiResponse.createResponse(response, SUCCESS_REGISTER_BOOKMARK);
        } else {
            return PetsTableApiResponse.createResponse(response, SUCCESS_DELETE_BOOKMARK);
        }
    }
}
