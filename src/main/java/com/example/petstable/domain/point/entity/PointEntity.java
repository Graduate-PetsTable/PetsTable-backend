package com.example.petstable.domain.point.entity;

import com.example.petstable.domain.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder()
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "points")
public class PointEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;
    private int point;

    /**
     * 추후 적립 내역 및 차감 내역 추가를 위한 확장성을 고려
     */
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    public static PointEntity createPoint(MemberEntity member) {
        return PointEntity.builder()
                .member(member)
                .point(0)
//                .transactionType(transactionType)
//                .description(description)
                .build();
    }

    public void increasePoint() {
        this.point += 10;
    }
}

