package com.example.petstable.domain.point.entity;

import com.example.petstable.domain.member.entity.BaseTimeEntity;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.point.message.PointMessage;
import com.example.petstable.global.exception.PetsTableException;
import jakarta.persistence.*;
import lombok.*;

import static com.example.petstable.domain.point.message.PointMessage.*;

@Entity
@Getter
@Builder()
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "points")
public class PointEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;
    private int point; // 적립 혹은 차감 시 잔액
    private int balance; // 잔액
    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    public void setMember(MemberEntity member) {
        this.member = member;
    }

    public void initBalance() {
        this.balance = 0;
    }

    public static PointEntity createPointEntity(MemberEntity member, int newBalance, int point, TransactionType transactionType, String description) {
        PointEntity pointEntity = PointEntity.builder()
                .member(member)
                .balance(newBalance)
                .point(point)
                .transactionType(transactionType)
                .description(description)
                .build();

        pointEntity.setMember(member);
        member.addPoints(pointEntity);
        return pointEntity;
    }
}
