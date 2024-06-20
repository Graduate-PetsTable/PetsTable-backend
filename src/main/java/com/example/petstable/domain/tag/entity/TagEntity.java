package com.example.petstable.domain.tag.entity;

import com.example.petstable.domain.board.entity.BoardEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "tag")
public class TagEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TagType type; // 태그 타입

    private String name; // 태그 이름

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity post;

    // 연관 관계 설정
    public void setPost(BoardEntity post) {
        this.post = post;
        post.getTags().add(this);
    }
}
