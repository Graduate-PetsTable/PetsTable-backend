package com.example.petstable.domain.bookmark.entity;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.member.entity.BaseTimeEntity;
import com.example.petstable.domain.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bookmark")
public class BookmarkEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity post;

    private boolean status; // 북마크 등록 여부

    // 연관 관계 메서드
    public void setMember(MemberEntity member) {
        this.member = member;
        member.getBookmarks().add(this);
    }

    public void setPost(BoardEntity post) {
        this.post = post;
        post.getBookmarks().add(this);
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static BookmarkEntity createBookmark(MemberEntity member, BoardEntity post) {
        BookmarkEntity bookmark = BookmarkEntity.builder()
                .member(member)
                .post(post)
                .status(true)
                .build();

        member.addBookmark(bookmark);
        post.addBookmark(bookmark);
        bookmark.setMember(member);
        bookmark.setPost(post);

        return bookmark;
    }

    public void removeFromMemberAndPost() {
        if (this.member != null) {
            this.member.getBookmarks().remove(this);
            this.member = null;
        }
        if (this.post != null) {
            this.post.getBookmarks().remove(this);
            this.post = null;
        }
    }
}
