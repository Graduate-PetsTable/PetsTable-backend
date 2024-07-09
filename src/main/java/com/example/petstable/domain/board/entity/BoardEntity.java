package com.example.petstable.domain.board.entity;

import com.example.petstable.domain.board.dto.response.BoardReadResponse;
import com.example.petstable.domain.member.entity.BaseTimeEntity;
import com.example.petstable.domain.member.entity.MemberEntity;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title; // 제목
    private String thumbnail_url; // 썸네일 ( 완성 사진 )

    private int view_count; // 조회수
    private int like_count; // 좋아요

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @OneToMany(mappedBy = "post")
    @BatchSize(size = 10)
    private List<DetailEntity> details; // 상세 설명

    @OneToMany(mappedBy = "post")
    private List<TagEntity> tags; // 게시글 태그 목록

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

    // 연관 관계 설정 - 회원
    public void setMember(MemberEntity member) {
        this.member = member;
        member.getPosts().add(this);
    }

    // 조회수 증가
    public void increaseViewCount() {
        this.view_count += 1;
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
                .title(boardEntity.getTitle())
                .imageUrl(boardEntity.getThumbnail_url())
                .tagName(tagNames)
                .build();
    }
}

