package com.example.petstable.domain.board.entity;

import com.example.petstable.domain.board.dto.request.BoardRequest;
import com.example.petstable.domain.board.dto.request.DetailRequest;
import com.example.petstable.domain.member.entity.BaseTimeEntity;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.tag.entity.TagEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "board")
public class BoardEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private List<DetailEntity> description; // 상세 설명

    @OneToMany(mappedBy = "post")
    private List<TagEntity> tags; // 게시글 태그 목록

    // 연관 관계 설정 - 상세 설명
    public void addDescriptions(List<DetailEntity> details) {
        if (description == null) {
            description = new ArrayList<>();
        }
        for (DetailEntity detail : details) {
            detail.setPost(this);
        }
        description.addAll(details);
    }

    // 연관 관계 설정 - 게시글 태그
    public void addTags(TagEntity tag) {
        tags.add(tag);
        tag.setPost(this);
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

    // 생성 메서드
    public static BoardEntity createPost(BoardRequest request, List<DetailRequest> detailRequests, TagEntity... tags) {
        BoardEntity boardEntity = BoardEntity.builder()
                .title(request.getTitle())
//                .thumbnail_url(request.getThumbnail_url())
                .build();

        // 상세 내용 연관 관계 설정
        List<DetailEntity> details = detailRequests.stream()
                .map(DetailEntity::createPostDetail)
                .collect(Collectors.toList());

        boardEntity.addDescriptions(details);

        // 게시글 태그 연관 관계 설정
        for (TagEntity tag : tags) {
            boardEntity.addTags(tag);
        }


        return boardEntity;
    }
}

