package com.example.petstable.domain.bookmark.controller;

import com.example.petstable.domain.bookmark.dto.response.BookmarkRegisterResponse;
import com.example.petstable.domain.bookmark.service.BookmarkService;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.petstable.domain.bookmark.message.BookmarkMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class BookmarkController implements BookmarkApi{
    private final BookmarkService bookmarkService;

    @PostMapping("/{postId}/bookmark")
    public PetsTableApiResponse<BookmarkRegisterResponse> addBookmark(@LoginUserId Long memberId, @PathVariable("postId") Long postId) {
        BookmarkRegisterResponse response = bookmarkService.registerBookmark(memberId, postId);
        if (response.isStatus()) {
            return PetsTableApiResponse.createResponse(response, SUCCESS_REGISTER_BOOKMARK);
        } else {
            return PetsTableApiResponse.createResponse(response, SUCCESS_DELETE_BOOKMARK);
        }
    }
}
