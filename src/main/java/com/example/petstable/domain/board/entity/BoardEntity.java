package com.example.petstable.domain.board.entity;

import com.example.petstable.domain.board.dto.response.BoardReadResponse;
import com.example.petstable.domain.bookmark.entity.BookmarkEntity;
import com.example.petstable.domain.member.entity.BaseTimeEntity;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.report.entity.ReportEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "board")
public class BoardEntity extends BaseTimeEntity {

    private static final int REPORT_POST_THRESHOLD_COUNT = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title; // 제목
    private String thumbnail_url; // 썸네일 ( 완성 사진 )
    private int reportCount; // 신고 횟수

    private int view_count; // 조회수

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @OneToMany(mappedBy = "post")
    @BatchSize(size = 10)
    private List<DetailEntity> details; // 상세 설명

    @OneToMany(mappedBy = "post")
    private List<TagEntity> tags; // 게시글 태그 목록

    @OneToMany(mappedBy = "post")
    private List<BookmarkEntity> bookmarks; // 북마크

    @OneToMany(mappedBy = "post")
    private List<ReportEntity> reports; // 신고

    // 연관 관계 설정 - 상세 설명
    public void addDetails(List<DetailEntity> detailEntities) {
        if (details == null) {
            details = new ArrayList<>();
        }
        for (DetailEntity detail : detailEntities) {
            detail.setPost(this);
        }
        details.addAll(detailEntities);
    }

    // 연관 관계 설정 - 게시글 태그
    public void addTags(List<TagEntity> tagList) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        for (TagEntity tag : tagList) {
            tag.setPost(this);
        }
        tags.addAll(tagList);
    }

    // 연관 관계 설정 - 북마크
    public void addBookmark(BookmarkEntity bookmark) {
        bookmarks.add(bookmark);
        bookmark.setPost(this);
    }

    // 게시글 태그 전체 삭제
    public void clearTags() {
        if (tags != null) {
            this.tags.clear();
        }
    }

    // 연관 관계 설정 - 회원
    public void setMember(MemberEntity member) {
        this.member = member;
        member.getPosts().add(this);
    }

    // 연관 관계 설정 - 신고
    public void addReport(ReportEntity report) {
        reports.add(report);
        report.setPost(this);
        increaseReportCount();
    }

    public boolean isMemberDeleted() {
        return this.member == null;
    }

    public boolean isWrittenBySelf(MemberEntity member) {
        return this.getMember() != null && this.getMember().equals(member);
    }

    public boolean hasAlreadyReported(MemberEntity member) {
        return this.reports.stream()
                .anyMatch(report -> report.getReporter().equals(member));
    }

    public boolean isReportThresholdExceeded() {
        return getReportCount() >= REPORT_POST_THRESHOLD_COUNT;
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public void clearPost() {
        this.title = null;
        this.thumbnail_url = null;
        if (this.details != null) {
            this.details.clear();
        }
        if (this.tags != null) {
            this.tags.clear();
        }
    }

    // 조회수 증가
    public void increaseViewCount() {
        this.view_count += 1;
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }

    // 썸네일 이미지 등록
    public void setThumbnail_url(String image_url) {
        this.thumbnail_url = image_url;
    }

    public static BoardReadResponse toBoardTitleAndTags(BoardEntity boardEntity) {
        List<String> tagNames = boardEntity.getTags().stream()
                .map(TagEntity::getName)
                .toList();

        return BoardReadResponse.builder()
                .id(boardEntity.getId())
                .title(boardEntity.getTitle())
                .imageUrl(boardEntity.getThumbnail_url())
                .tagName(tagNames)
                .build();
    }
}

