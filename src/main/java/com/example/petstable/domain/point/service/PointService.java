package com.example.petstable.domain.point.service;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.point.dto.response.PointResponse;
import com.example.petstable.domain.point.entity.PointEntity;
import com.example.petstable.domain.point.entity.TransactionType;
import com.example.petstable.domain.point.repository.PointRepository;
import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.petstable.domain.point.message.PointMessage.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public PointResponse getPointHistoryByMemberId(Long memberId) {
        PointEntity pointEntity = pointRepository.findByMemberId(memberId).orElseThrow(
                () -> new PetsTableException(POINT_NOT_FOUND.getStatus(), POINT_NOT_FOUND.getMessage(), 404));

        return PointResponse.builder()
                .point(pointEntity.getPoint())
                .description(pointEntity.getDescription())
                .transactionType(pointEntity.getTransactionType().getDescription())
                .createTime(pointEntity.getCreatedTime())
                .build();
    }

    public PointResponse getPointBalance(Long memberId) {
        PointEntity point = pointRepository.findFirstByMemberIdOrderByCreatedTimeDesc(memberId);
        int balance = (point != null) ? point.getBalance() : 0;
        return PointResponse.builder()
                .point(balance)
                .build();
    }

    @Transactional
    public void increasePoints(MemberEntity member, int point, String description) {
        int currentBalance = getPointBalance(member.getId()).getPoint();
        int newBalance = currentBalance + point;

        PointEntity pointEntity = PointEntity.createPointEntity(
                member,
                newBalance,
                point,
                TransactionType.POINT_GAINED,
                description
        );
        pointRepository.save(pointEntity);
    }

    @Transactional
    public void subtractPoints(MemberEntity member, int points, String description) {
        int currentBalance = getPointBalance(member.getId()).getPoint();

        if (currentBalance < points) {
            throw new PetsTableException(INSUFFICIENT_BALANCE.getStatus(), INSUFFICIENT_BALANCE.getMessage(), 400);
        }

        int newBalance = currentBalance - points;

        PointEntity pointEntity = PointEntity.createPointEntity(
                member,
                newBalance,
                -points,
                TransactionType.POINT_USED,
                description
        );

        member.addPoints(pointEntity);
        pointRepository.save(pointEntity); // 포인트 차감 내역 저장
    }
}