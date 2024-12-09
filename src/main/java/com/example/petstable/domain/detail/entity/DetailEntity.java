package com.example.petstable.domain.detail.entity;

import com.example.petstable.domain.board.entity.BoardEntity;
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

    public static DetailEntity create(String imageUrl, String description, BoardEntity recipe) {
        DetailEntity detailEntity = DetailEntity.builder()
                .image_url(imageUrl)
                .description(description)
                .post(recipe)
                .build();
        detailEntity.setPost(recipe);
        return detailEntity;
    }

    // 연관관계 설정
    public void setPost(BoardEntity post) {
        this.post = post;
        post.getDetails().add(this);
    }

    // 이미지만 업데이트
    public void updateImageUrl(String newImageUrl) {
        this.image_url = newImageUrl;
    }

    // 설명만 업데이트
    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    // 이미지, 설명 둘 다 업데이트
    public void updateImageUrlAndDescription(String newImageUrl, String newDescription) {
        this.image_url = newImageUrl;
        this.description = newDescription;
    }
}
