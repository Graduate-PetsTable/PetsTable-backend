package com.example.petstable.domain.bookmark.service;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.repository.BoardRepository;
import com.example.petstable.domain.bookmark.dto.response.BookmarkRegisterResponse;
import com.example.petstable.domain.bookmark.entity.BookmarkEntity;
import com.example.petstable.domain.bookmark.repository.BookmarkRepository;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.petstable.domain.board.message.BoardMessage.POST_NOT_FOUND;
import static com.example.petstable.domain.member.message.MemberMessage.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public BookmarkRegisterResponse registerBookmark(Long memberId, Long postId) {

        Optional<BookmarkEntity> optionalBookmark = bookmarkRepository.findByMemberIdAndPostId(memberId, postId);
        boolean status = false;

        if (optionalBookmark.isPresent()) { // 이미 존재하면 북마크 삭제
            BookmarkEntity bookmark = optionalBookmark.get();
            bookmark.removeFromMemberAndPost();
            bookmarkRepository.delete(bookmark);

        } else {
            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

            BoardEntity post = boardRepository.findById(postId)
                    .orElseThrow(() -> new PetsTableException(POST_NOT_FOUND.getStatus(), POST_NOT_FOUND.getMessage(), 404));

            BookmarkEntity bookmark = BookmarkEntity.createBookmark(member, post);
            bookmarkRepository.save(bookmark);
            status = true;
        }

        return BookmarkRegisterResponse.builder()
                .memberId(memberId)
                .postId(postId)
                .status(status)
                .build();
    }
}
