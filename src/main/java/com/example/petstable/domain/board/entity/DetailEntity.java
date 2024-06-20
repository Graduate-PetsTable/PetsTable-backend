package com.example.petstable.domain.board.entity;

import com.example.petstable.domain.board.dto.request.DetailRequest;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "detail")
public class DetailEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "detail_id")
    private Long id;

    private String image_url; // 사진
    private String description; // 설명

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity post;

    // 연관관계 설정
    public void setPost(BoardEntity post) {
        this.post = post;
        post.getDescription().add(this);
    }

    // 생성 메서드
    public static DetailEntity createPostDetail(DetailRequest request) {
        return DetailEntity.builder()
                .image_url(request.getImage_url())
                .description(request.getDescription())
                .build();
    }
}
