package com.example.petstable.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "ingredient")
public class IngredientEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long id;

    private String name;
    private String weight;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity post;

    // 연관 관계 설정
    public void setPost(BoardEntity post) {
        this.post = post;
        post.getIngredients().add(this);
    }
}
